package Timetable.model;

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

    public Pane getPane() {
        VBox root = new VBox();
        root.getStyleClass().add("auditorium-pane");
        root.setFillWidth(true);

        HBox top = new HBox();
        top.setFillHeight(true);
//        top.getStyleClass().add("auditorium-pane");
        var image = new Image("auditorium.jpg");
        var imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

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

        root.getChildren().add(top);
        return root;
    }

}
