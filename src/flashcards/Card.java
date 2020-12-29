package flashcards;

public class Card {
    private final String term;
    private final String definition;
    private int errors;

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
        this.errors = 0;
    }

    public Card(String term, String definition, int errors) {
        this.term = term;
        this.definition = definition;
        this.errors = errors;
    }

    public String getDefinition() {
        return definition;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public void resetErrors() {
        errors = 0;
    }

    public String getTerm() {
        return term;
    }

    public void increaseErrorCount() {
        errors++;
    }

    public int getErrors() {
        return errors;
    }
}
