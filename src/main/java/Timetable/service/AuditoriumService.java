package Timetable.service;

import Timetable.model.Auditorium;
import Timetable.model.AuditoriumProperty;
import Timetable.model.Pair;
import Timetable.repositories.AuditoriumRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuditoriumService {
    @NonNull
    private final AuditoriumRepository auditoriumRepository;

    private final PairService pairService;

    @Autowired
    public AuditoriumService(@NonNull final AuditoriumRepository auditoriumRepository,
                             @Lazy PairService pairService) {
        this.auditoriumRepository = auditoriumRepository;
        this.pairService = pairService;
    }

    @NonNull
    public Auditorium save(@NonNull final Auditorium auditorium) {
        return auditoriumRepository.save(auditorium);
    }

    public void delete(@NonNull final Auditorium auditorium) {auditoriumRepository.delete(auditorium);}

    @NonNull
    public ObservableList<Auditorium> getAuditoriums() {
        return FXCollections.observableArrayList(auditoriumRepository.findAll());
    }

    @NonNull
    public ObservableList<Auditorium> searchAuditoriums(@NonNull final String text) {
        return FXCollections.observableArrayList(auditoriumRepository.findByNameIgnoreCaseContaining(text));
    }

    @NonNull
    public Auditorium searchCreateAuditorium(@NonNull final String text) {
        var result = searchAuditoriums(text);
        if (result.size() > 0) {
            return result.get(0);
        }
        var auditorium = new Auditorium();
        auditorium.setName(text);

        if (text.contains("П")) {
            auditorium.setMaxStudents(100);
        } else {
            auditorium.setMaxStudents(40);
        }
        save(auditorium);
        return auditorium;
    }

    @NonNull
    public ObservableList<Auditorium> getAvailableAuditoriums(@NonNull final LocalDateTime beginTime,
                                                              @NonNull final LocalDateTime endTime) {
        return FXCollections.observableArrayList(auditoriumRepository.findAvailableAuditorium(beginTime, endTime));
    }

    @NonNull
    public ObservableList<Auditorium> getTopAvailableAuditoriums(@NonNull final LocalDateTime beginTime,
                                                              @NonNull final LocalDateTime endTime,
                                                              int limit) {
        return FXCollections.observableArrayList(auditoriumRepository.findAvailableAuditorium(
                beginTime, endTime, new PageRequest(0, limit)
        ));
    }

    @NonNull
    public ObservableList<Auditorium> getTopMaxAvailableAuditoriums(
            @NonNull final LocalDateTime beginTime,
            @NonNull final LocalDateTime endTime,
            @NonNull final int maxStudents,
            int limit) {
        return FXCollections.observableArrayList(auditoriumRepository.findAvailableAuditorium(
                beginTime, endTime, maxStudents, new PageRequest(0, limit)
        ));
    }

    @Nullable
    public Auditorium getAuditoriumByName(@NonNull final String name) {
        final ObservableList<Auditorium> auditoriums = searchAuditoriums(name);
        if (auditoriums.isEmpty()) {
            return null;
        } else {
            return auditoriums.get(0);
        }
    }

    @NonNull
    public ObservableList<Auditorium> filterAuditoriums(@NonNull final String name,
                                                        @NonNull final int maxStudents,
                                                        @NonNull final Set<AuditoriumProperty> properties) {
        return FXCollections.observableArrayList(properties.isEmpty()?
                auditoriumRepository.findByNameIgnoreCaseContainingAndMaxStudentsGreaterThanEqual(name, maxStudents):
                auditoriumRepository.
                findByNameIgnoreCaseContainingAndMaxStudentsGreaterThanEqualAndPropertiesIn(name, maxStudents, properties));
    }

    @NonNull
    public Pane getAuditoriumPane(@NonNull final StackPane container,
                                  @NonNull final Auditorium auditorium) {
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
        info.setAlignment(Pos.TOP_CENTER);
        info.prefWidthProperty().bind(root.widthProperty());
        Label name = new Label(auditorium.getName());
        name.setMaxHeight(Double.MAX_VALUE);
        name.getStyleClass().add("auditorium-name");
        Separator separator = new Separator();
        separator.setPrefWidth(name.getPrefWidth());
        separator.getStyleClass().add("auditorium-separator");
        List<Label> infoLabels = auditorium.getProperties().stream().map(
                auditoriumProperty -> new Label(auditoriumProperty.getName())).collect(Collectors.toList());
        info.getChildren().addAll(name, separator);
//        info.getChildren().addAll(infoLabels);
        VBox infoVbox = new VBox();
        infoVbox.getChildren().addAll(infoLabels);
        infoVbox.setAlignment(Pos.CENTER);

        top.getChildren().addAll(imageView, info);
        HBox availability = getAuditoriumAvailability(pairService, auditorium);

        GridPane lower = new GridPane();
        lower.add(availability, 0, 0);
        lower.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 2);
        lower.add(infoVbox, 1, 0, 2, 2);
        lower.prefWidthProperty().bind(root.widthProperty());
        lower.setPadding(new Insets(0, 10, 0, 10));
        ColumnConstraints grow = new ColumnConstraints();
        grow.setHgrow(Priority.ALWAYS);
        ColumnConstraints stay = new ColumnConstraints();
        stay.setHgrow(Priority.SOMETIMES);
        lower.getColumnConstraints().addAll(grow, stay, grow);

        root.getChildren().addAll(top, lower);

        return root;
    }

    public HBox getAuditoriumAvailability(PairService pairService, Auditorium auditorium) {
        HBox root = new HBox();
        root.setSpacing(3);
        root.setPadding(new Insets(0, 10, 0,0));
        ObservableList<Pair> pairs = FXCollections.observableArrayList(auditorium.getPairs());

        for (int dayIndex = 0; dayIndex < DateService.daysOfWeek.size(); ++dayIndex) {
            String dayName = DateService.daysOfWeek.get(dayIndex);

            VBox node = new VBox();
            VBox availability = new VBox();
            LocalTime endTime = LocalTime.of(21, 0);
            for (LocalTime beginTime = LocalTime.of(9, 0); beginTime.compareTo(endTime) < 0;
                 beginTime = beginTime.plusHours(2)) {
                Pane pane = new Pane();
                pane.setPrefSize(11, 8);
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
//            availability.prefWidthProperty().bind(root.widthProperty());
            root.getChildren().add(node);
        }
        return root;
    }
}
