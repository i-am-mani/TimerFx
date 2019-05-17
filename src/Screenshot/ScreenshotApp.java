package Screenshot;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Logger;

public class ScreenshotApp extends Application {
    private final static Logger log = Logger.getLogger(ScreenshotApp.class.getName());
    double xPos, yPos;
    Stage stage;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if (stage == null) {
            stage = primaryStage;
            AnchorPane root = new AnchorPane();

            Rectangle rect = createDraggableRectangle(200, 200, 400, 300);
            rect.setFill(Color.LIGHTBLUE);

            root.getChildren().add(rect);


            Scene scene = new Scene(root, 800, 800);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    stage.toBack();
                    captureScreenShot((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                }
            });
            scene.setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    stage.toFront();
                }
            });


            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            root.setStyle("-fx-background-color:transparent;");

            primaryStage.setFullScreen(true);
            primaryStage.show();
        } else {
            stage.show();
        }

    }

    private Rectangle createDraggableRectangle(double x, double y, double width, double height) {
        final double handleRadius = 10;

        Rectangle rect = new Rectangle(x, y, width, height);
        rect.setOpacity(0.2);

        // top left resize handle:
        Circle resizeHandleNW = new Circle(handleRadius, Color.GOLD);
        // bind to top left corner of Rectangle:
        resizeHandleNW.centerXProperty().bind(rect.xProperty());
        resizeHandleNW.centerYProperty().bind(rect.yProperty());

        // bottom right resize handle:
        Circle resizeHandleSE = new Circle(handleRadius, Color.GOLD);
        // bind to bottom right corner of Rectangle:
        resizeHandleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
        resizeHandleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));

        //close handle:
        Text close = new Text("X");
        close.setFill(Color.RED);
        close.xProperty().bind(rect.xProperty().add(rect.widthProperty()));
        close.yProperty().bind(rect.yProperty());
        close.setOnMouseClicked(event -> {
            stage.close();
        });

        // force circles to live in same parent as rectangle:
        rect.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Node c : Arrays.asList(resizeHandleNW, resizeHandleSE, close)) {
                Pane currentParent = (Pane) c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane) newParent).getChildren().add(c);
            }

        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(resizeHandleNW, mouseLocation);
        setUpDragging(resizeHandleSE, mouseLocation);
        setUpDragging(rect, mouseLocation);

        resizeHandleNW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX;
                if (newX >= handleRadius
                        && newX <= rect.getX() + rect.getWidth() - handleRadius) {
                    rect.setX(newX);
                    rect.setWidth(rect.getWidth() - deltaX);
                }
                double newY = rect.getY() + deltaY;
                if (newY >= handleRadius
                        && newY <= rect.getY() + rect.getHeight() - handleRadius) {
                    rect.setY(newY);
                    rect.setHeight(rect.getHeight() - deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeHandleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newMaxX = rect.getX() + rect.getWidth() + deltaX;
                if (newMaxX >= rect.getX()
                        && newMaxX <= rect.getParent().getBoundsInLocal().getWidth()) {
                    rect.setWidth(rect.getWidth() + deltaX);
                }
                double newMaxY = rect.getY() + rect.getHeight() + deltaY;
                if (newMaxY >= rect.getY()
                        && newMaxY <= rect.getParent().getBoundsInLocal().getHeight()) {
                    rect.setHeight(rect.getHeight() + deltaY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        rect.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();
                double newX = rect.getX() + deltaX;
                double newMaxX = newX + rect.getWidth();
                if (newMaxX <= rect.getParent().getBoundsInLocal().getWidth() - handleRadius) {
                    rect.setX(newX);
                }
                double newY = rect.getY() + deltaY;
                double newMaxY = newY + rect.getHeight();
                if (newMaxY <= rect.getParent().getBoundsInLocal().getHeight() - handleRadius) {
                    rect.setY(newY);
                }
                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

        });

        return rect;
    }

    private void setUpDragging(Node node, Wrapper<Point2D> mouseLocation) {

        node.setOnDragDetected(event -> {
            node.getParent().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        node.setOnMouseReleased(event -> {
            node.getParent().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null;
        });
    }

    public void captureScreenShot(int x, int y, int width, int height) {
        java.awt.Rectangle screenRect = new java.awt.Rectangle(x, y, width, height);
        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy-hhmmss.SSS"));
        try {
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            ImageIO.write(capture, "png", new File("img-" + datetime + ".png"));
            log.info("Screenshot Captured");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void toFront() {
        stage.toFront();
    }

    public boolean isShowing() {
        return stage.isShowing();
    }
    static class Wrapper<T> {
        T value;
    }


}