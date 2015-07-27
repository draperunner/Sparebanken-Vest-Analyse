package main;

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

    private List<String> groceryPlaces = Arrays.asList("REMA", "ICA", "BUNNPRIS", "SIT KAFE", "SIT STORKIOSK", "SESAM",
            "MENY", "RIMI", "SZECHUAN AS", "SERVERINGSGJENG", "NARVESEN", "JOKER");

    List<Transaction> transactions = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    /**
     *
     * @param file: The file of transactions to load
     *
     * Scans the given file and fills the transa
     */
    public Analysis(File file) {

        // Instantiate scanner
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
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
            }
            else {
                transaction.setValue(new BigDecimal(parts[parts.length - 2].replace(',', '.')));
                transaction.setArchiveReference(parts[parts.length - 1]);
            }

            transactions.add(transaction);
        }

        /**
         *
         * DEFINE POSTS
         *
         * Posts defined under will automatically be added to tables and charts
         *
         */

        // Balance
        Post balance = new Post("balance", "Balanse", Post.Type.OTHER, transactions, t -> t.stream()
            .collect(Collectors.toList())
        );
        PostOperator medianOperator = transactions1 -> {
            List<YearMonth> months = getPeriodInMonths();
            List<BigDecimal> monthlyBalances = months.stream().map(m -> {
                List<Transaction> transThatMonth = transactions1.stream().filter(t -> m.equals(t.getYearMonthOfBookDate())).collect(Collectors.toList());
                return balance.getTotal(transThatMonth);
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
            .filter(tr -> groceryPlaces.stream().anyMatch(substring -> tr.getDescription().contains(substring)))
            .collect(Collectors.toList()));
        groceriesExpenses.setShowAsPositive(true);
        posts.add(groceriesExpenses);

        // Salary
        Post salary = new Post("salary", "Løn", Post.Type.INCOME, transactions, t -> t.stream()
            .filter(tr -> tr.getTextCode().equalsIgnoreCase("LØNN"))
            .collect(Collectors.toList()));
        posts.add(salary);

        // Stipend / loan
        Post stipend = new Post("stipend", "Stipend/Lån", Post.Type.INCOME, transactions, t -> t.stream()
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

    public Post getPost(String name) {
        return posts.stream().filter(post -> post.getName().equals(name)).findAny().get();
    }
}
