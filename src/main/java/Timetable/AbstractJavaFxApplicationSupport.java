package Timetable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.io.IOException;

public abstract class AbstractJavaFxApplicationSupport extends Application {

    private static String[] savedArgs;
    private Stage splashScreen;

    protected ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        Platform.runLater(this::showSplash);
        context = SpringApplication.run(getClass(), savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
        Platform.runLater(this::closeSplash);
    }

    /**
     * Загружаем заставку обычным способом. Выставляем везде прозрачность
     */
    private void showSplash() {
        try {
            splashScreen = new Stage(StageStyle.TRANSPARENT);
            splashScreen.setTitle("Splash");
            final Parent root = FXMLLoader.load(getClass().getResource("../splash.fxml"));
            final Scene scene = new Scene(root, Color.TRANSPARENT);
            splashScreen.setScene(scene);
            splashScreen.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Закрывает сцену с заставкой
     */
    private void closeSplash() {
        splashScreen.close();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        context.close();
    }

    protected static void launchApp(@NonNull final Class<? extends AbstractJavaFxApplicationSupport> clazz, // claZZ?
                                    @NonNull final String[] args) {
        AbstractJavaFxApplicationSupport.savedArgs = args;
        Application.launch(clazz, args);
    }
}
