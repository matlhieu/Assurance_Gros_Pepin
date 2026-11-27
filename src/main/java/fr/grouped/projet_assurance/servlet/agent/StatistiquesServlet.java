package fr.grouped.projet_assurance.servlet.agent;

import fr.grouped.projet_assurance.dao.ContratDAO;
import fr.grouped.projet_assurance.dao.PaiementDAO;
import fr.grouped.projet_assurance.dao.SinistreDAO;
import fr.grouped.projet_assurance.dao.impl.ContratDAOImpl;
import fr.grouped.projet_assurance.dao.impl.PaiementDAOImpl;
import fr.grouped.projet_assurance.dao.impl.SinistreDAOImpl;
import fr.grouped.projet_assurance.model.Contrat;
import fr.grouped.projet_assurance.model.Paiement;
import fr.grouped.projet_assurance.model.Sinistre;
import fr.grouped.projet_assurance.model.enums.StatutContrat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Statistiques du portefeuille (agent et admin). Les graphiques sont des barres en CSS : aucune librairie externe. */
@WebServlet({"/agent/statistiques", "/admin/statistiques"})
public class StatistiquesServlet extends HttpServlet {

    private final ContratDAO contratDAO = new ContratDAOImpl();
    private final SinistreDAO sinistreDAO = new SinistreDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final fr.grouped.projet_assurance.dao.UtilisateurDAO utilisateurDAO = new fr.grouped.projet_assurance.dao.impl.UtilisateurDAOImpl();

    /** Une barre de graphique : libellé, valeur affichée, pourcentage de la plus grande barre. */
    public record Barre(String libelle, String valeur, long pourcentage) {
        public String getLibelle() { return libelle; }
        public String getValeur() { return valeur; }
        public long getPourcentage() { return pourcentage; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Contrat> contrats = contratDAO.findAllAvecDetails();
        List<Sinistre> sinistres = sinistreDAO.findAllAvecDetails();
        List<Paiement> paiements = paiementDAO.findAll();

        BigDecimal encaisse = somme(paiements, "EFFECTUE");
        BigDecimal enAttente = somme(paiements, "EN_ATTENTE");
        BigDecimal indemnise = sinistres.stream().filter(s -> s.getMontantIndemnise() != null)
                .map(Sinistre::getMontantIndemnise).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal primesActives = contrats.stream().filter(c -> c.getStatut() == StatutContrat.ACTIF)
                .map(Contrat::getPrime).reduce(BigDecimal.ZERO, BigDecimal::add);
        // Ratio sinistres / primes : indemnités versées rapportées aux cotisations encaissées
        long ratio = encaisse.signum() == 0 ? 0
                : indemnise.multiply(BigDecimal.valueOf(100)).divide(encaisse, 0, RoundingMode.HALF_UP).longValue();

        req.setAttribute("encaisse", encaisse);
        req.setAttribute("enAttente", enAttente);
        req.setAttribute("indemnise", indemnise);
        req.setAttribute("primesActives", primesActives);
        req.setAttribute("ratio", ratio);
        req.setAttribute("nbContratsActifs", contrats.stream().filter(c -> c.getStatut() == StatutContrat.ACTIF).count());
        req.setAttribute("nbContrats", contrats.size());
        req.setAttribute("nbSinistres", sinistres.size());

        // Clients
        int nbClients = utilisateurDAO.findByRole("CLIENT").size();
        long nbClientsCouverts = contrats.stream().filter(c -> c.getStatut() == StatutContrat.ACTIF)
                .map(c -> c.getUtilisateur().getIdUtilisateur()).distinct().count();
        req.setAttribute("nbClients", nbClients);
        req.setAttribute("nbClientsCouverts", nbClientsCouverts);

        // Répartition des clients par type d'assurance (un client compte une fois par produit)
        Map<String, Long> clientsParType = new TreeMap<>();
        contrats.stream()
                .map(c -> c.getTypeAssurance().getNom() + "|" + c.getUtilisateur().getIdUtilisateur())
                .distinct()
                .forEach(k -> clientsParType.merge(k.substring(0, k.indexOf('|')), 1L, Long::sum));
        req.setAttribute("clientsParType", barres(clientsParType, false));

        // Évolution du nombre de contrats : nouveaux contrats par mois (date d'effet), 12 derniers mois
        DateTimeFormatter fmtCourt = DateTimeFormatter.ofPattern("MMM yy", Locale.FRANCE);
        Map<String, Long> nouveauxContrats = new LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth m = YearMonth.now().minusMonths(i);
            nouveauxContrats.put(m.format(fmtCourt), contrats.stream()
                    .filter(c -> c.getDateDebut() != null && YearMonth.from(c.getDateDebut()).equals(m)).count());
        }
        req.setAttribute("evolutionContrats", barres(nouveauxContrats, false));

        // Contrats par produit
        Map<String, Long> parType = new TreeMap<>();
        contrats.forEach(c -> parType.merge(c.getTypeAssurance().getNom(), 1L, Long::sum));
        req.setAttribute("contratsParType", barres(parType, false));

        // Contrats par statut
        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutContrat st : StatutContrat.values()) {
            parStatut.put(st.getLibelle(), contrats.stream().filter(c -> c.getStatut() == st).count());
        }
        req.setAttribute("contratsParStatut", barres(parStatut, false));

        // Sinistres par nature
        Map<String, Long> parNature = new TreeMap<>();
        sinistres.forEach(s -> parNature.merge(s.getTypeSinistre() == null ? "Autre" : s.getTypeSinistre(), 1L, Long::sum));
        req.setAttribute("sinistresParNature", barres(parNature, false));

        // Encaissements des 6 derniers mois
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRANCE);
        Map<String, Long> parMois = new LinkedHashMap<>();
        YearMonth courant = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = courant.minusMonths(i);
            long centimes = paiements.stream()
                    .filter(p -> "EFFECTUE".equals(p.getStatut()) && YearMonth.from(p.getDatePaiement()).equals(m))
                    .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .movePointRight(2).longValue();
            parMois.put(m.format(fmt), centimes);
        }
        req.setAttribute("encaissementsParMois", barres(parMois, true));

        req.getRequestDispatcher("/WEB-INF/views/agent/statistiques.jsp").forward(req, resp);
    }

    /** Transforme des valeurs en barres proportionnelles à la plus grande. */
    private static List<Barre> barres(Map<String, Long> valeurs, boolean centimes) {
        long max = valeurs.values().stream().mapToLong(Long::longValue).max().orElse(0);
        List<Barre> res = new ArrayList<>();
        valeurs.forEach((k, v) -> res.add(new Barre(k,
                centimes ? fr.grouped.projet_assurance.util.WebUtil.euros(BigDecimal.valueOf(v, 2)) : String.valueOf(v),
                max == 0 ? 0 : Math.round(v * 100.0 / max))));
        return res;
    }

    private static BigDecimal somme(List<Paiement> paiements, String statut) {
        return paiements.stream().filter(p -> statut.equals(p.getStatut()))
                .map(Paiement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
