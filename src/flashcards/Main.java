package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static List<Card> cardList = new ArrayList<>(); //Keeps track off all the cards that get created or imported
    public static Scanner scanner = new Scanner(System.in);
    public static StringBuilder stringBuilder = new StringBuilder(); //uses to keep track of the log and creates a file at the end.
    public static Map<String, String> arguments = new HashMap<>();

    public static void main(String[] args) {
        //Checks the command line for arguments and puts them in a hashmap for performing actions on launch
        if (args.length != 0) {
            for (int i = 0; i < args.length; i += 2) {
                arguments.put(args[i], args[i + 1]);
            }
            //if the command line includes the import command, it uses the importFile method to perform launch actions
            if (arguments.containsKey("-import")) {
                importFile(arguments.get("-import"));
            }
        }

        while (true) {
            //Prints the instructions on a loop until the user exits the programme
            printText("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats)");
            String command = scanner.nextLine();
            stringBuilder.append(command).append("\n");
            switch (command) {
                case "add":
                    addCard();
                    continue;
                case "remove":
                    removeCard();
                    continue;
                case "import":
                    importFile();
                    continue;
                case "export":
                    exportFile();
                    continue;
                case "ask":
                    ask();
                    continue;
                case "exit":
                    printText("Bye bye!");
                    if (arguments.containsKey("-export")) exportFile(arguments.get("-export"));
                    break;
                case "log":
                    log();
                    continue;
                case "hardest card":
                    getHardestCards();
                    continue;
                case "reset stats":
                    resetStats();
                    continue;
                default:
                    printText("Invalid command");
                    continue;
            }
            break;
        }
    }

    //this method writes out to the console but also adds to the stringbuilder for logging purposes

    public static void printText(String string) {
        System.out.println(string);
        stringBuilder.append(string).append("\n");
    }

    //resets all the cards error count to zero
    private static void resetStats() {
        if (!cardList.isEmpty()) {
            for (Card card : cardList) {
                card.resetErrors();
            }
            printText("Card statistics have been reset");
        }
    }

    //This method finds the cards with the most errors
    private static void getHardestCards() {
        List<Card> hardestCardsList = new ArrayList<>();
        if (cardList.isEmpty()) {
            printText("There are no cards with errors");
        } else {
            //Finds the card with the biggest error count
            int biggest = 0;
            for (Card card : cardList) {
                if (card.getErrors() > biggest) {
                    biggest = card.getErrors();
                }
            }
            if (biggest == 0) {
                System.out.println("There are no cards with errors");
            } else {
                //Checks if there are any other cards with the same error count and adds it to a new list
                for (Card card : cardList) {
                    if (card.getErrors() == biggest) {
                        hardestCardsList.add(card);
                    }
                }
                //StringBuilder to prepare the message depending on the variables
                StringBuilder errorStringBuilder = new StringBuilder();
                if (hardestCardsList.size() == 1) {
                    errorStringBuilder.append("The hardest card is \"").append(hardestCardsList.get(0).getTerm())
                            .append("\". You have ").append(biggest);
                    if (biggest == 1) {
                        errorStringBuilder.append(" error answering it.");
                    } else {
                        errorStringBuilder.append(" errors answering it.");
                    }
                } else {
                    errorStringBuilder.append("The hardest cards are ");
                    for (int i = 0; i < hardestCardsList.size(); i++) {
                        errorStringBuilder.append("\"").append(hardestCardsList.get(i).getTerm()).append("\"");
                        if (i != hardestCardsList.size() - 1) {
                            errorStringBuilder.append(", ");
                        }
                    }
                    errorStringBuilder.append(". You have ").append(biggest);
                    if (biggest == 1) {
                        errorStringBuilder.append(" error answering them.");
                    } else {
                        errorStringBuilder.append(" errors answering them.");
                    }
                }
                printText(errorStringBuilder.toString());
            }
        }
    }

    private static void log() {
        //Exports the log of the programme to a specified file
        printText("File name");
        String fileName = scanner.nextLine();
        stringBuilder.append(fileName).append("\n");
        File file = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(file)) {
            String log = stringBuilder.toString();
            fileWriter.append(log);
            printText("The log has been saved.");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    //Method to find the term from a specified definition
    public static Card getCardByDefinition(String definition) {
        for (Card card : cardList) {
            if (card.getDefinition().equals(definition)) {
                return card;
            }
        }
        return null;
    }

    //Finds a card based on a search query matching it to the term
    public static Card getCardByTerm(String term) {
        for (Card card : cardList) {
            if (card.getTerm().equals(term)) {
                return card;
            }
        }
        return null;
    }

    //Checks if a card exists based on the term
    public static boolean cardExists(String term) {
        for (Card card : cardList) {
            if (card.getTerm().equals(term)) {
                return true;
            }
        }
        return false;
    }

    public static int getIndexOfCard(String term) {
        return cardList.indexOf(getCardByTerm(term));
    }

    public static boolean definitionIsUnique(String definition) {
        for (Card card : cardList) {
            if (card.getDefinition().equals(definition)) {
                return false;
            }
        }
        return true;
    }


    public static void ask() {
        //iterates through each card and asks for the definition
        printText("How many times to ask?");
        int guesses = Integer.parseInt(scanner.nextLine());
        stringBuilder.append(guesses).append("\n");
        Random random = new Random();
        for (int i = 0; i < guesses; i++) {
            Card card = cardList.get(random.nextInt(cardList.size()));
            printText("Print the definition of \"" + card.getTerm() + "\":");
            String guess = scanner.nextLine();
            stringBuilder.append(guess).append("\n");
            if (guess != null && guess.equals(card.getDefinition())) {
                printText("Correct!");
            } else {
                String term = (getCardByDefinition(guess) != null) ? Objects.requireNonNull(getCardByDefinition(guess)).getTerm() : "";
                if (cardExists(term)) {
                    printText("Wrong. The right answer is \"" + card.getDefinition() + "\" but your definition is the correct for \"" + term + "\"");
                } else {
                    printText("Wrong. The right answer is \"" + card.getDefinition() + "\".");
                }
                card.increaseErrorCount();
            }
        }
    }

    //Creates a card based on user input, makes sure both the term and definition are unique
    public static void addCard() {
        String term;
        String definition;
        printText("The card:");
        //Makes sure the entry key is unique
        term = scanner.nextLine();
        stringBuilder.append(term).append("\n");
        if (cardExists(term)) {
            printText("The card \"" + term + "\" already exists.");
        } else {
            printText("The definition of the card:");
            definition = scanner.nextLine();
            stringBuilder.append(definition).append("\n");
            if (!definitionIsUnique(definition)) {
                printText("The definition \"" + definition + "\" already exists.");
            } else {
                cardList.add(new Card(term, definition));
                printText("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
            }
        }
    }

    public static void removeCard() {
        printText("Which card?");
        String cardName = scanner.nextLine();
        stringBuilder.append(cardName).append("\n");
        if (!cardExists(cardName)) {
            printText("Can't remove \"" + cardName + "\": there is no such card.");
        } else {
            cardList.remove(getCardByTerm(cardName));
            printText("The card has been removed");
        }
    }

    //Uses Scanner to read each line from a text file, splitting it and creating cards on the go.
    public static void importFile(String fileName) {
        File file = new File(fileName);
        if (!file.isFile()) {
            System.out.println("File not found");
        } else {
            try (Scanner fileScanner = new Scanner(file)) {
                int incrementer = 0;
                while (fileScanner.hasNextLine()) {
                    String[] line = fileScanner.nextLine().split(":");
                    String term = line[0];
                    String definition = line[1];
                    int errors = Integer.parseInt(line[2]);
                    if (cardExists(line[0])) {
                        cardList.set(getIndexOfCard(term), new Card(term, definition, errors));
                    } else {
                        cardList.add(new Card(line[0], line[1], Integer.parseInt(line[2])));
                    }
                    incrementer++;
                }
                fileScanner.close();
                printText(incrementer + " cards have been loaded.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void importFile() {
        printText("File name:");
        String sourceFileName = scanner.nextLine();
        stringBuilder.append(sourceFileName).append("\n");
        importFile(sourceFileName);
    }

    //Exports all the cards to a specified file in the format: "term:definition:errors"
    public static void exportFile(String fileName) {
        File file = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(file)) {
            int incrementer = 0;
            for (Card card : cardList) {
                fileWriter.append(card.getTerm()).append(":").append(card.getDefinition()).append(":").append(String.valueOf(card.getErrors())).append("\n");
                incrementer++;
            }
            fileWriter.flush();
            fileWriter.close();
            printText(incrementer + " cards have been saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportFile() {
        printText("File name:");
        String targetFileName = scanner.nextLine();
        stringBuilder.append(targetFileName).append("\n");
        exportFile(targetFileName);
    }
}
