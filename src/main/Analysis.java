package main;

import main.utils.DateUtils;
import main.utils.FileUtils;
import main.utils.ListUtils;
import main.utils.NumberUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by mats on 22.07.2015.
 */
public class Analysis {

    private List<String> groceryPlaces = Arrays.asList(
            "REMA", "ICA", "BUNNPRIS", "SIT KAFE", "SIT STORKIOSK", "MENY", "RIMI", "NARVESEN", "JOKER", "KIWI");
    private List<String> restaurants = Arrays.asList(
            "SZECHUAN AS", "SESAM", "ALPINO", "SMILE PIZZA", "LA FIESTA", "HARD ROCK CAFE", "UPPER CRUST");
    private List<String> nightlifePlaces = Arrays.asList(
            "CROWBAR", "GOSSIP", "DATTERA TIL HAGEN", "SERVERINGSGJENG", "CAFE 3B", "CAFE MONO", "RAMP PUB", "TRONDHEIM MIKRO",
            "FIRE FINE", "GOOD OMENS");
    private List<String> publicTransport = Arrays.asList(
            "ATB", "RUTER", "BUSS", "FLYTOGET", "FLYBUSS", "NSB");
    private List<String> streaming = Arrays.asList(
            "SPOTIFY", "NETFLIX", "HBO");
    private List<String> phoneBills = Arrays.asList(
            "TELEREGNING");

    private List<Transaction> transactions = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    /**
     *
     * @param file: The file of transactions to load
     *
     * Scans the given file and fills the transactions list
     */
    public Analysis(File file) {

        // Instantiate scanner
        Scanner fileScanner;
        String encoding = FileUtils.guessEncoding(file);
        try {
            fileScanner = new Scanner(file, encoding);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Ignore first line which only contains headers
        if (fileScanner.hasNextLine()) {
            fileScanner.nextLine();
        }

        // Fill transactions list
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            String[] parts = line.trim().split("\\s+");

            Transaction transaction = new Transaction();
            transaction.setBookDate(DateUtils.stringToLocalDate(parts[0]));
            transaction.setInterestDate(DateUtils.stringToLocalDate(parts[1]));
            transaction.setTextCode(parts[2]);

            boolean textCodeContainsTwoWords = false;
            if (parts[2].equalsIgnoreCase("VISA")) {
                transaction.setTextCode(transaction.getTextCode() + " " + parts[3]);
                textCodeContainsTwoWords = true;
            }
            int descriptionStartIndex = (textCodeContainsTwoWords) ? 4 : 3;

            boolean offsetAccountIsIncluded = NumberUtils.isValidAccountNumber(parts[parts.length - 1]);
            int descriptionEndIndex = (offsetAccountIsIncluded) ? parts.length - 3 : parts.length - 2;

            StringBuilder description = new StringBuilder();
            for (int i = descriptionStartIndex; i < descriptionEndIndex; i++) {
                description.append(parts[i]);
                description.append(" ");
            }
            transaction.setDescription(description.toString());

            if (offsetAccountIsIncluded) {
                transaction.setValue(new BigDecimal(parts[parts.length - 3].replace(',', '.')));
                transaction.setArchiveReference(parts[parts.length - 2]);
                transaction.setOffsetAccount(parts[parts.length - 1]);
            } else {
                transaction.setValue(new BigDecimal(parts[parts.length - 2].replace(',', '.')));
                transaction.setArchiveReference(parts[parts.length - 1]);
            }
            transactions.add(transaction);
        }
        definePosts();
    }

    /**
     *
     * DEFINE POSTS
     *
     * Posts defined under will automatically be added to tables and charts
     *
    */
    private void definePosts() {
        // Balance
        Post balance = new Post("balance", "Balanse", Post.Type.OTHER, transactions, t -> t.stream()
            .collect(Collectors.toList()));
        PostOperator medianOperator = transactions1 -> {
            List<YearMonth> months = getPeriodInMonths();
            List<BigDecimal> monthlyBalances = months.stream().map(m -> {
                List<Transaction> transactionsThatMonth = getTransactionsOfMonth(m);
                return balance.getTotal(transactionsThatMonth);
            }).collect(Collectors.toList());
            return ListUtils.getMedian(monthlyBalances);
        };
        balance.setMedianOperator(medianOperator);
        posts.add(balance);

        // Expenses. Type is OTHER and not EXPENSE because this is the total.
        Post expenses = new Post("expenses", "Utgifter", Post.Type.OTHER, transactions, t -> t.stream()
            .filter(transaction -> NumberUtils.isNegative(transaction.getValue()))
            .collect(Collectors.toList()));
        expenses.setShowAsPositive(true);
        posts.add(expenses);

        // Income. Type is OTHER and not INCOME because this is the total.
        Post income = new Post("income", "Inntekter", Post.Type.OTHER, transactions, t -> t.stream()
            .filter(transaction -> NumberUtils.isPositive(transaction.getValue()))
            .collect(Collectors.toList())
        );
        posts.add(income);

        // Grocery expenses
        Post groceriesExpenses = new Post("groceriesExpenses", "Dagligvareutgifter", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> groceryPlaces.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(groceriesExpenses);

        // Restaurant expenses
        Post restaurantExpenses = new Post("restaurantExpenses", "Restaurantar", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> restaurants.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(restaurantExpenses);

        // Nightlife expenses
        Post nightlifeExpenses = new Post("nightlifeExpenses", "Nattliv", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> nightlifePlaces.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(nightlifeExpenses);

        // Public transport expenses
        Post publicTransportExpenses = new Post("publicTransportExpenses", "Kollektivtrafikk", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> publicTransport.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(publicTransportExpenses);

        // Streaming expenses
        Post streamingExpenses = new Post("streamingExpenses", "Streaming", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> streaming.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(streamingExpenses);

        // Phone bills
        Post phoneBillExpenses = new Post("phoneBillExpenses", "Telefonrekningar", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> phoneBills.stream().anyMatch(substring -> tr.getDescription().contains(substring.toUpperCase())))
            .collect(Collectors.toList()));
        posts.add(phoneBillExpenses);

        // Rent expenses
        Post rentExpenses = new Post("rentExpenses", "Leige", Post.Type.EXPENSE, transactions, t -> t.stream()
            .filter(tr -> tr.getDescription().toUpperCase().contains("LEIGE") || tr.getDescription().toUpperCase().contains("LEIE"))
                .collect(Collectors.toList()));
        posts.add(rentExpenses);

        // Salary
        Post salary = new Post("salary", "Løn", Post.Type.INCOME, transactions, t -> t.stream()
            .filter(tr -> tr.getTextCode().equalsIgnoreCase("LØNN"))
            .collect(Collectors.toList()));
        posts.add(salary);

        // Lånekassen
        Post stipend = new Post("stipend", "Lånekassen", Post.Type.INCOME, transactions, t -> t.stream()
            .filter(tr -> tr.getDescription().contains("STATENS LÅNEKASSE"))
            .collect(Collectors.toList()));
        posts.add(stipend);
    }

    public BigDecimal getMonthly(String postName, YearMonth month) {
        List<Transaction> transactionsThatMonth = transactions.stream()
            .filter(t -> t.getYearMonthOfBookDate().equals(month))
            .collect(Collectors.toList());
        return getPost(postName).getTotal(transactionsThatMonth);
    }

    public LocalDate getStartDate() {
        return transactions.stream()
            .map(Transaction::getBookDate)
            .min(LocalDate::compareTo)
            .get();
    }

    public LocalDate getEndDate() {
        return transactions.stream()
            .map(Transaction::getBookDate)
            .max(LocalDate::compareTo)
            .get();
    }

    public List<YearMonth> getPeriodInMonths() {
        return DateUtils.getYearMonthsBetween(getStartDate(), getEndDate());
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction> getTransactionsOfMonth(YearMonth month) {
        return transactions.stream().filter(t -> month.equals(t.getYearMonthOfBookDate())).collect(Collectors.toList());
    }

    public Post getPost(String name) {
        return posts.stream().filter(post -> post.getName().equals(name)).findAny().get();
    }
}
