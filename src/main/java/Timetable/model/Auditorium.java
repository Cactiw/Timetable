package Timetable.model;

import Timetable.service.AuditoriumService;
import Timetable.service.DateService;
import Timetable.service.PairService;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;

@Entity
@Component
public class Auditorium {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column
    private String name;
    @Column
    private Integer maxStudents;

    @Type( type = "json" )
    @Column( columnDefinition = "json" )
    private Map<String, Integer> additional;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Map<String, Integer> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Integer> additional) {
        this.additional = additional;
    }

    public Pane getPane(PairService pairService) {
        VBox root = new VBox();
        root.getStyleClass().add("auditorium-pane");
        root.setFillWidth(true);
        root.setSpacing(15);

        HBox top = new HBox();
        top.setFillHeight(true);
        var image = new Image("auditorium.jpg");
        var imageView = new ImageView(image);
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);

        var info = new VBox();
        info.setFillWidth(true);
        info.setAlignment(Pos.CENTER);
        info.prefWidthProperty().bind(root.widthProperty());
        Label name = new Label(this.getName());
        name.setMaxHeight(Double.MAX_VALUE);
        name.getStyleClass().add("auditorium-name");
        Separator separator = new Separator();
        separator.setPrefWidth(name.getPrefWidth());
        separator.getStyleClass().add("auditorium-separator");
        Label infoLabel = new Label("Test info");
        info.getChildren().addAll(name, separator, infoLabel);

        top.getChildren().addAll(imageView, info);
        HBox availability = getAvailability(pairService);
        availability.prefWidthProperty().bind(root.widthProperty());
        root.getChildren().addAll(top, availability);
        return root;
    }

    public HBox getAvailability(PairService pairService) {
        HBox root = new HBox();
        root.setSpacing(1);
        ObservableList<Pair> pairs = pairService.getAuditoriumPairs(this);

        for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
            String dayName = DateService.daysOfWeek.get(dayIndex);

            VBox node = new VBox();
            VBox availability = new VBox();
            LocalTime endTime = LocalTime.of(21, 0);
            for (LocalTime beginTime = LocalTime.of(9, 0); beginTime.compareTo(endTime) < 0;
                 beginTime = beginTime.plusHours(2)) {
                Pane pane = new Pane();
                pane.setPrefSize(30, 15);
                LocalTime finalBeginTime = beginTime;
                int finalDayIndex = dayIndex;
                pane.getStyleClass().add(pairs.filtered(
                        pair -> pairService.checkConflict(pair, finalDayIndex + 1, finalBeginTime, endTime)).size() > 0 ?
                        "auditorium-busy": "auditorium-free");
                availability.getChildren().add(pane);
            }
            Label dayLabel = new Label(dayName.substring(0, 1));
            dayLabel.alignmentProperty().set(Pos.CENTER);
            dayLabel.prefWidthProperty().bind(node.widthProperty());
            node.getChildren().addAll(availability, dayLabel);
            availability.prefWidthProperty().bind(root.widthProperty());
            root.getChildren().add(node);
        }
        return root;
    }

}
