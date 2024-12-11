package ma.emsi.abourabia.jeetp1abourabia.JSF;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.*;

@Named
@ViewScoped
public class ChatBeanTabourabia implements Serializable {

    /**
     * Rôle "système" que l'on attribuera plus tard à un LLM.
     * Possible d'ajouter de nouveaux rôles dans la méthode getSystemRoles.
     */
    private String systemRole;
    /**
     * Quand le rôle est choisi par l'utilisateur dans la liste déroulante,
     * il n'est plus possible de le modifier (voir code de la page JSF) dans la même session de chat.
     */
    private boolean systemRoleChangeable = true;

    /**
     * Dernière question posée par l'utilisateur.
     */
    private String question;
    /**
     * Dernière réponse de l'API OpenAI.
     */
    private String reponse;
    private String texteRequeteJson;
    private String texteReponseJson;
    private boolean debug;
    /**
     * La conversation depuis le début.
     */
    private StringBuilder conversation = new StringBuilder();

    /**
     * Contexte JSF. Utilisé pour qu'un message d'erreur s'affiche dans le formulaire.
     */
    @Inject
    private FacesContext facesContext;

    /**
     * Obligatoire pour un bean CDI (classe gérée par CDI).
     */
    public ChatBeanTabourabia() {
    }

    public String getTexteRequeteJson() {
        return texteRequeteJson;
    }

    public void setTexteRequeteJson(String texteRequeteJson) {
        this.texteRequeteJson = texteRequeteJson;
    }

    public String getTexteReponseJson() {
        return texteReponseJson;
    }

    public void setTexteReponseJson(String texteReponseJson) {
        this.texteReponseJson = texteReponseJson;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void toggleDebug() {
        this.setDebug(!isDebug());
    }
    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    public boolean isSystemRoleChangeable() {
        return systemRoleChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    /**
     * setter indispensable pour le textarea.
     *
     * @param reponse la réponse à la question.
     */
    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * Envoie la question au serveur.
     * En attendant de l'envoyer à un LLM, le serveur fait un traitement quelconque, juste pour tester :
     * Le traitement consiste à copier la question en minuscules et à l'entourer avec "||". Le rôle système
     * est ajouté au début de la première réponse.
     *
     * @return null pour rester sur la même page.
     */
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }

        // Nouveau traitement : Analyse linguistique et jeux de mots
        this.reponse = genererReponseOriginale(question);

        if (this.conversation.isEmpty()) {
            this.reponse = "🧠 Mode d'analyse linguistique : " + systemRole.toUpperCase(Locale.FRENCH) + "\n" + this.reponse;
            this.systemRoleChangeable = false;
        }

        afficherConversation();
        return null;
    }

    private String genererReponseOriginale(String texte) {
        // Analyse et transformation linguistique
        return String.format("""
        🔍 Analyse linguistique de votre texte :
        
        📏 Longueur : %d caractères
        📊 Statistiques :
        - Mots : %d
        - Voyelles : %d
        - Consonnes : %d
        
        🎭 Jeux de mots :
        - Mot le plus long : %s
        - Mot le plus court : %s
        
        🧩 Défi linguistique :
        %s
        
        🎲 Bonus créatif :
        %s
        """,
                texte.length(),
                compterMots(texte),
                compterVoyelles(texte),
                compterConsonnes(texte),
                trouverMotPlusLong(texte),
                trouverMotPlusCourt(texte),
                genererDefiLinguistique(texte),
                genererBonusCreatif()
        );
    }

    private int compterMots(String texte) {
        return texte.trim().split("\\s+").length;
    }

    private int compterVoyelles(String texte) {
        return texte.toLowerCase().replaceAll("[^aeiouéèàù]", "").length();
    }

    private int compterConsonnes(String texte) {
        return texte.toLowerCase().replaceAll("[^bcdfghjklmnpqrstvwxz]", "").length();
    }

    private String trouverMotPlusLong(String texte) {
        return Arrays.stream(texte.split("\\s+"))
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    private String trouverMotPlusCourt(String texte) {
        return Arrays.stream(texte.split("\\s+"))
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

    private String genererDefiLinguistique(String texte) {
        String[] defiOptions = {
                "🧩 Trouvez un anagramme du mot le plus long !",
                "🔄 Inversez l'ordre des syllabes du mot le plus court.",
                "📝 Écrivez une phrase avec tous les mots commençant par la même lettre.",
                "🎭 Transformez votre texte en charade !",
                "🔠 Réécrivez votre phrase en utilisant uniquement des mots de 4 lettres."
        };
        return defiOptions[new Random().nextInt(defiOptions.length)];
    }

    private String genererBonusCreatif() {
        String[] bonus = {
                "🌈 Votre créativité linguistique est votre plus grand atout !",
                "🚀 Chaque mot est une aventure, chaque phrase un voyage !",
                "🧠 Le langage est un puzzle, et vous en êtes le maître !",
                "✨ Votre imagination transforme les mots en magie !",
                "🎨 La langue est votre terrain de jeu créatif !"
        };
        return bonus[new Random().nextInt(bonus.length)];
    }
    /**
     * Pour un nouveau chat.
     * Termine la portée view en retournant "index" (la page index.xhtml sera affichée après le traitement
     * effectué pour construire la réponse) et pas null. null aurait indiqué de rester dans la même page (index.xhtml)
     * sans changer de vue.
     * Le fait de changer de vue va faire supprimer l'instance en cours du backing bean par CDI et donc on reprend
     * tout comme au début puisqu'une nouvelle instance du backing va être utilisée par la page index.xhtml.
     * @return "index"
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Pour afficher la conversation dans le textArea de la page JSF.
     */
    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getSystemRoles() {
        List<SelectItem> listeSystemRoles = new ArrayList<>();
        // Ces rôles ne seront utilisés que lorsque la réponse sera données par un LLM.
        String role = """
                You are a helpful assistant. You help the user to find the information they need.
                If the user type a question, you answer it.
                """;
        listeSystemRoles.add(new SelectItem(role, "Assistant"));
        role = """
                You are an interpreter. You translate from English to French and from French to English.
                If the user type a French text, you translate it into English.
                If the user type an English text, you translate it into French.
                If the text contains only one to three words, give some examples of usage of these words in English.
                """;
        // 1er argument : la valeur du rôle, 2ème argument : le libellé du rôle
        listeSystemRoles.add(new SelectItem(role, "Traducteur Anglais-Français"));
        role = """
                Your are a travel guide. If the user type the name of a country or of a town,
                you tell them what are the main places to visit in the country or the town
                are you tell them the average price of a meal.
                """;
        listeSystemRoles.add(new SelectItem(role, "Guide touristique"));

        this.systemRole = (String) listeSystemRoles.getFirst().getValue();
        return listeSystemRoles;
    }
}
