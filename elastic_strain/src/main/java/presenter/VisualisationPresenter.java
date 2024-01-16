package presenter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import model.Solver;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class VisualisationPresenter implements Initializable {

    @FXML
    private LineChart<Number, Number> plot;

    @FXML
    private Button button;

    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private final Solver solver = new Solver();

    @FXML
    private Label timeLabel;

    private final DecimalFormat df = new DecimalFormat("#.###");;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        plot.setCreateSymbols(false);
        this.df.setRoundingMode(RoundingMode.CEILING);
    }


    @FXML
    private void generatePlot(){

        if (!plot.getData().isEmpty()){
            plot.getData().remove(0);
        }

        XYChart.Series<Number,Number> series = calculateSolution(spinner.getValue());
        plot.getData().add(0, series);
        series.getNode().setStyle("-fx-stroke: darkblue;");
    }

    private XYChart.Series<Number,Number> calculateSolution(int n){

        long start = System.nanoTime();
        double[] coefficients = solver.solve(n);
        long stop = System.nanoTime();
        double elapsed = (stop-start)/1e9;
        timeLabel.setText(df.format(elapsed)+" [s]");
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        double h = solver.getH();
        for(int i=0;i<coefficients.length;i++){
            double x = h*i;
            series.getData().add(new XYChart.Data<>(x, coefficients[i]));
        }
        return series;
    }
}
