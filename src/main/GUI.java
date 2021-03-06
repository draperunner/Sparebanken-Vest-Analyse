package main; /**
 * Created by mats on 22.07.2015.
 */

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.utils.DateUtils;
import main.utils.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GUI extends Application {

    private Stage stage;

    private Analysis analysis;
    private ObservableList<TransactionTableRow> transactions = FXCollections.observableArrayList();

    @FXML private TextField fileTextField;

    @FXML private Label statusLabel;

    @FXML private TableView<ObservableList<StringProperty>> overviewTable;

    @FXML private TableView<TransactionTableRow> transactionTable;
    @FXML private TableColumn<String, String> bookDateColumn;
    @FXML private TableColumn<String, String> interestDateColumn;
    @FXML private TableColumn<String, String> textCodeColumn;
    @FXML private TableColumn<String, String> descriptionColumn;
    @FXML private TableColumn<String, String> archiveReferenceColumn;
    @FXML private TableColumn<String, String> offsetAccountColumn;
    @FXML private TableColumn<String, String> valueColumn;

    @FXML private LineChart<String, Double> lineChart;
    @FXML private Slider pieChartSlider;
    @FXML private PieChart pieChartBalance;
    @FXML private PieChart pieChartExpenses;
    @FXML private PieChart pieChartIncome;

    private List<List<PieChart.Data>> pieChartBalanceData, pieChartExpensesData, pieChartIncomeData;
    private List<File> selectedFiles = new ArrayList<>();
    private int previousSliderValue = 0;

    public static void main(String[] args) {
        launch(GUI.class);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        try {
            VBox page = FXMLLoader.load(GUI.class.getResource("gui.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sparebanken Vest Analyseverktøy");
            primaryStage.show();
        } catch (IOException e) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    public void browse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Finn transaksjonsfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("csv", "*.csv"),
                new FileChooser.ExtensionFilter("txt", "*.txt"),
                new FileChooser.ExtensionFilter("Alle filer", "*.*")
        );
        selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (selectedFiles == null) {
            return;
        }
        if (selectedFiles.size() == 1) {
            fileTextField.setText(selectedFiles.get(0).getAbsolutePath());
        }
        else if (selectedFiles.size() > 1) {
            fileTextField.setText(selectedFiles.stream().map(File::getName).reduce((a, b) -> a + ", " + b).get());
        }
        else if (selectedFiles.size() > 0) {
            statusLabel.setText("Trykk 'Start analyse' for å starte.");
        }
    }

    @FXML
    public void startAnalysis() {
        statusLabel.setText("Analyserer...");

        // If no files have been selected by FileChooser, see if the user has written a valid path in the text field
        if (selectedFiles.isEmpty()) {
            String filePath = fileTextField.getText();
            // No filepath is set
            if (filePath.isEmpty()) {
                statusLabel.setText("Inga fil er vald.");
                return;
            }
            File file = new File(filePath);
            // File is not readable, and it is not possible to set it to be readable.
            if (!file.canRead() && !file.setReadable(true)) {
                statusLabel.setText("Fila kan ikkje lesast.");
                return;
            }
            selectedFiles.add(file);
        }

        // Analyze!
        analysis = new Analysis();
        selectedFiles.forEach(file -> {
            String name = file.getName();
            try {
                analysis.analyze(file);
            }
            catch (Exception e) {
                statusLabel.setText("Fila " + name + " er ikkje gyldig. Ver venleg og vel ei .csv- eller .txt-fil.");
                e.printStackTrace();
            }
        });

        // If there are no transactions
        if (analysis.getTransactions().isEmpty()) {
            statusLabel.setText("Det ser ut som at fila/filene er tom(me). " +
                "Sjå til at ho/dei er på teiknsettet UTF-8, windows-1252 eller ISO-8859-1.");
            return;
        }

        updateStatus();
        // Initialize and fill tables and charts with data
        initOverviewTable();
        initLineChart();
        initPieCharts();
        initTransactionTable();
    }

    void initOverviewTable() {
        // Add period column first
        TableColumn<ObservableList<StringProperty>, String> periodColumn = createColumn(0, "Periode");
        overviewTable.getColumns().add(periodColumn);

        // Add a column for each post
        List<Post> posts = analysis.getPosts();
        for (int i = 0; i < posts.size(); i++) {
            TableColumn<ObservableList<StringProperty>, String> column = createColumn(i+1, posts.get(i).getNorwegianName());
            overviewTable.getColumns().add(column);
        }

        // Add rows for each month
        analysis.getPeriodInMonths().stream()
            .forEach(month -> {
                ObservableList<StringProperty> data = FXCollections.observableArrayList();
                data.add(new SimpleStringProperty(month.toString()));
                analysis.getPosts().stream().forEach(post -> {
                    BigDecimal value = analysis.getMonthly(post.getName(), month);
                    data.add(new SimpleStringProperty(value.toString()));
                });
                overviewTable.getItems().add(data);
            });

        // Add rows for total, average and median values
        ObservableList<StringProperty> totalData = FXCollections.observableArrayList();
        ObservableList<StringProperty> averageData = FXCollections.observableArrayList();
        ObservableList<StringProperty> medianData = FXCollections.observableArrayList();
        totalData.add(new SimpleStringProperty("Totalt"));
        averageData.add(new SimpleStringProperty("Gjennomsnittleg"));
        medianData.add(new SimpleStringProperty("Median"));

        analysis.getPosts().stream().forEach(post -> {
            totalData.add(new SimpleStringProperty(post.getTotal().toString()));
            averageData.add(new SimpleStringProperty(post.getAverage().toString()));
            medianData.add(new SimpleStringProperty(post.getMedian().toString()));
        });
        overviewTable.getItems().addAll(Arrays.asList(totalData, averageData, medianData));
    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setText(columnTitle);
        column.setCellValueFactory(cellDataFeatures -> {
            ObservableList<StringProperty> values = cellDataFeatures.getValue();
            if (columnIndex >= values.size()) {
                return new SimpleStringProperty("");
            } else {
                return cellDataFeatures.getValue().get(columnIndex);
            }
        });
        return column;
    }

    void initTransactionTable() {
        bookDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookDate"));
        interestDateColumn.setCellValueFactory(new PropertyValueFactory<>("interestDate"));
        textCodeColumn.setCellValueFactory(new PropertyValueFactory<>("textCode"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        archiveReferenceColumn.setCellValueFactory(new PropertyValueFactory<>("archiveReference"));
        offsetAccountColumn.setCellValueFactory(new PropertyValueFactory<>("offsetAccount"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        analysis.getTransactions().stream().forEach(transaction -> transactions.add(new TransactionTableRow(transaction)));
        transactionTable.setItems(transactions);
    }

    void initLineChart() {
        lineChart.setTitle("Månadleg transaksjonsoversikt");

        // HashMap containing a Series (line on the chart) for each main.Post in analysis
        HashMap<String, XYChart.Series<String, Double>> seriesMap = new HashMap<>();

        // Add a series for each main.Post
        analysis.getPosts().stream().forEach(post -> {
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            series.setName(post.getNorwegianName());
            seriesMap.put(post.getName(), series);
        });

        // Add monthly data from each post to each series
        analysis.getPeriodInMonths().stream().forEach(month -> analysis.getPosts().stream().forEach(post -> {
            // Expenses are shown as positive numbers for easier comparison with income
            double value = analysis.getMonthly(post.getName(), month).doubleValue();
            if (post.getShowAsPositive()) {
                value = Math.abs(value);
            }
            XYChart.Data<String, Double> data = new XYChart.Data<>(month.toString(), value);
            seriesMap.get(post.getName()).getData().add(data);
        }));

        // Add the series to the chart
        lineChart.getData().addAll(analysis.getPosts().stream()
                .map(post -> seriesMap.get(post.getName()))
                .collect(Collectors.toList()));
    }

    void initPieCharts() {
        List<YearMonth> months = analysis.getPeriodInMonths();

        // Configure slider
        pieChartSlider.setMin(0);
        pieChartSlider.setMax(months.size() + 2);
        pieChartSlider.setMinorTickCount(0);
        pieChartSlider.setMajorTickUnit(1);
        pieChartSlider.setSnapToTicks(true);
        pieChartSlider.setShowTickMarks(true);
        pieChartSlider.setShowTickLabels(true);
        pieChartSlider.setLabelFormatter(new PieChartSliderStringConverter(months));

        pieChartBalanceData = new ArrayList<>(months.size());
        pieChartExpensesData = new ArrayList<>(months.size());
        pieChartIncomeData = new ArrayList<>(months.size());

        for (int i = 0; i < months.size() + 3; i++) {
            // Totals for this month
            BigDecimal totalExpenses, totalIncome;

            // Values of each expense and income post this month
            HashMap<Post, BigDecimal> expensesMap = new HashMap<>();
            HashMap<Post, BigDecimal> incomeMap = new HashMap<>();

            // Put posts in appropriate lists based on type
            analysis.getPosts().stream().forEach(post -> {
                if (post.getType() == Post.Type.EXPENSE) {
                    expensesMap.put(post, new BigDecimal(0));
                } else if (post.getType() == Post.Type.INCOME) {
                    incomeMap.put(post, new BigDecimal(0));
                }
            });

            // Total
            if (i == months.size()) {
                totalExpenses = analysis.getPost("expenses").getTotal().abs();
                totalIncome = analysis.getPost("income").getTotal().abs();
                expensesMap.keySet().stream().forEach(post -> expensesMap.put(post, post.getTotal().abs()));
                incomeMap.keySet().stream().forEach(post -> incomeMap.put(post, post.getTotal().abs()));
            }
            // Average
            else if (i == months.size() + 1) {
                totalExpenses = BigDecimal.ZERO;
                totalIncome = BigDecimal.ZERO;
                for (Post post : expensesMap.keySet()) {
                    totalExpenses = totalExpenses.add(analysis.getPeriodInMonths().stream()
                        .map(month -> post.getAverage(analysis.getTransactionsOfMonth(month)).abs())
                        .reduce(BigDecimal::add).get());
                    expensesMap.put(post, post.getAverage().abs());
                }
                for (Post post : incomeMap.keySet()) {
                    totalIncome = totalIncome.add(analysis.getPeriodInMonths().stream()
                        .map(month -> post.getAverage(analysis.getTransactionsOfMonth(month)).abs())
                        .reduce(BigDecimal::add).get());
                    incomeMap.put(post, post.getAverage().abs());
                }
            }
            // Median
            else if (i == months.size() + 2) {
                totalExpenses = BigDecimal.ZERO;
                totalIncome = BigDecimal.ZERO;
                for (Post post : expensesMap.keySet()) {
                    totalExpenses = totalExpenses.add(analysis.getPeriodInMonths().stream()
                        .map(month -> post.getMedian(analysis.getTransactionsOfMonth(month)).abs())
                        .reduce(BigDecimal::add).get());
                    expensesMap.put(post, post.getMedian().abs());
                }
                for (Post post : incomeMap.keySet()) {
                    totalIncome = totalIncome.add(analysis.getPeriodInMonths().stream()
                        .map(month -> post.getMedian(analysis.getTransactionsOfMonth(month)).abs())
                        .reduce(BigDecimal::add).get());
                    incomeMap.put(post, post.getMedian().abs());
                }
            }
            // Monthly total
            else {
                YearMonth month = months.get(i);
                totalExpenses = analysis.getMonthly("expenses", month).abs();
                totalIncome = analysis.getMonthly("income", month).abs();
                expensesMap.keySet().stream().forEach(post -> expensesMap.put(post, analysis.getMonthly(post.getName(), month).abs()));
                incomeMap.keySet().stream().forEach(post -> incomeMap.put(post, analysis.getMonthly(post.getName(), month).abs()));
            }

            BigDecimal hundred = new BigDecimal(100);
            int scale = 1;

            BigDecimal sumOfExpensesAndIncome = totalExpenses.add(totalIncome);
            BigDecimal expenseRatio = NumberUtils.roundedDivision(totalExpenses, sumOfExpensesAndIncome, BigDecimal.ZERO)
                .multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);
            BigDecimal incomeRatio = NumberUtils.roundedDivision(totalIncome, sumOfExpensesAndIncome, BigDecimal.ZERO)
                .multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);

            HashMap<String, BigDecimal> expensesRatios = new HashMap<>();
            HashMap<String, BigDecimal> incomeRatios = new HashMap<>();
            BigDecimal otherExpenses = totalExpenses.abs();
            BigDecimal otherIncome = totalIncome.abs();

            // Create pie slices for each expense except "total" and "other"
            for (Post post : expensesMap.keySet()) {
                BigDecimal value = expensesMap.get(post).abs();
                otherExpenses = otherExpenses.subtract(value);
                BigDecimal ratio = NumberUtils.roundedDivision(value, totalExpenses, BigDecimal.ZERO);
                ratio = ratio.multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);
                String label = post.getNorwegianName() + " " + ratio.toPlainString() + "%";
                expensesRatios.put(label, ratio);
            }
            // Create pie slices for each income except "total" and "other"
            for (Post post : incomeMap.keySet()) {
                BigDecimal value = incomeMap.get(post).abs();
                otherIncome = otherIncome.subtract(value);
                BigDecimal ratio = NumberUtils.roundedDivision(value, totalIncome, BigDecimal.ZERO);
                ratio = ratio.multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);
                String label = post.getNorwegianName() + " " + ratio.toPlainString() + "%";
                incomeRatios.put(label, ratio);
            }

            // Create pie slices for "other" expenses and income
            BigDecimal otherExpensesRatio = NumberUtils.roundedDivision(otherExpenses, totalExpenses, BigDecimal.ZERO);
            otherExpensesRatio = otherExpensesRatio.multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);
            BigDecimal otherIncomeRatio = NumberUtils.roundedDivision(otherIncome, totalIncome, BigDecimal.ZERO);
            otherIncomeRatio = otherIncomeRatio.multiply(hundred).setScale(scale, BigDecimal.ROUND_HALF_UP);
            expensesRatios.put("Andre utgifter " + otherExpensesRatio + "%", otherExpensesRatio);
            incomeRatios.put("Andre inntekter " + otherIncomeRatio + "%", otherIncomeRatio);

            // Create pie slices for total expenses and income (Leftmost pie chart)
            List<PieChart.Data> balanceData = new ArrayList<>();
            balanceData.add(new PieChart.Data("Utgifter " + expenseRatio.toPlainString() + "%", expenseRatio.doubleValue()));
            balanceData.add(new PieChart.Data("Inntekter " + incomeRatio.toPlainString() + "%", incomeRatio.doubleValue()));
            pieChartBalanceData.add(balanceData);

            // Create pie slices for expenses (Middle pie chart)
            List<PieChart.Data> expensesData = new ArrayList<>(expensesRatios.keySet().size());
            expensesRatios.keySet().stream()
                .forEach(post -> expensesData.add(new PieChart.Data(post, expensesRatios.get(post).doubleValue())));
            pieChartExpensesData.add(expensesData);

            // Create pie slices for income (Rightmost pie chart)
            List<PieChart.Data> incomeData = new ArrayList<>(incomeRatios.keySet().size());
            incomeRatios.keySet().stream()
                .forEach(post -> incomeData.add(new PieChart.Data(post, incomeRatios.get(post).doubleValue())));
            pieChartIncomeData.add(incomeData);
        }

        pieChartBalance.getData().setAll(pieChartBalanceData.get(0));
        pieChartExpenses.getData().setAll(pieChartExpensesData.get(0));
        pieChartIncome.getData().setAll(pieChartIncomeData.get(0));

        updatePieCharts(0d);
        // Set title and data for pie charts when slider value changes
        pieChartSlider.valueProperty().addListener((observable, oldValue, newValue) -> updatePieCharts(newValue));
    }

    void updatePieCharts(Number newValue) {
        double value = newValue.doubleValue();
        int intValue = newValue.intValue();
        if (previousSliderValue == intValue || pieChartSlider.isValueChanging()) {
            return;
        }
        previousSliderValue = intValue;

        pieChartBalance.setTitle("Balanse " + pieChartSlider.getLabelFormatter().toString(value));
        pieChartBalance.getData().clear();
        pieChartBalance.getData().addAll(pieChartBalanceData.get(intValue));

        pieChartExpenses.setTitle("Utgifter " + pieChartSlider.getLabelFormatter().toString(value));
        pieChartExpenses.getData().clear();
        pieChartExpenses.getData().addAll(pieChartExpensesData.get(intValue));

        pieChartIncome.setTitle("Inntekter " + pieChartSlider.getLabelFormatter().toString(value));
        pieChartIncome.getData().clear();
        pieChartIncome.getData().addAll(pieChartIncomeData.get(intValue));
    }

    void updateStatus() {
        int numberOfTransactions = analysis.getTransactions().size();
        LocalDate startDate = analysis.getStartDate();
        LocalDate endDate = analysis.getEndDate();
        long days = DateUtils.getDaysBetween(startDate, endDate);
        statusLabel.setText(numberOfTransactions + " transaksjonar frå " + startDate + " til " + endDate
                + " (" + days + " dagar).");
    }
}
