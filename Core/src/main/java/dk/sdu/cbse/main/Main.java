package dk.sdu.cbse.main;


import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(ModuleConfig.class)) {


            Game game = context.getBean(Game.class);


            game.start(primaryStage);
            game.render();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}