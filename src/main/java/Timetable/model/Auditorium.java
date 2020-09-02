package Timetable.model;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
        HBox top = new HBox();
        var image = new Image("auditorium.jpg");
        var imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        var info = new VBox();
        info.getChildren().addAll(new Label(this.getName()), new Line(), new Label("Test info"));

        top.getChildren().addAll(imageView, info);

        root.getChildren().add(top);
        return root;
    }

}
