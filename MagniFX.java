//package javafxapplication18;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
///public class JavaFXApplication18 extends Application {
public class MagniFX extends Application {

    private ImageView bgImageView = new ImageView();
    private Image image;
    private Scene scene;
    private DoubleProperty magnification = new SimpleDoubleProperty();
    private DoubleProperty GLASS_SIZE = new SimpleDoubleProperty();
    private DoubleProperty GLASS_CENTER = new SimpleDoubleProperty();
    private DoubleProperty centerX = new SimpleDoubleProperty();
    private DoubleProperty centerY = new SimpleDoubleProperty();
    private DoubleProperty factor = new SimpleDoubleProperty();
    private DoubleProperty viewportCenterX = new SimpleDoubleProperty();
    private DoubleProperty viewportCenterY = new SimpleDoubleProperty();
    private DoubleProperty viewportSize = new SimpleDoubleProperty();
    private ImageView magGlass = new ImageView();
    private Group glassGroup = new Group();
    private Text desc = new Text();

    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DoubleBinding db = new DoubleBinding() {

            {
                super.bind(centerX, factor);
            }

            @Override
            protected double computeValue() {
                return centerX.get() * factor.get();
            }
        };
        DoubleBinding db2 = new DoubleBinding() {

            {
                super.bind(centerY, factor);
            }

            @Override
            protected double computeValue() {
                return centerY.get() * factor.get();
            }
        };
        viewportCenterX.bind(db);
        viewportCenterY.bind(db2);
        image = new Image(this.getClass().getResourceAsStream("/pond.jpg"));

        Pane root = new Pane();
        scene = new Scene(root, 900, 700);

        setupBgImageView();
        setupFactor();
        setupGLASS_SIZE();
        magnification.setValue(4);//1.5

        DoubleBinding db3 = new DoubleBinding() {

            {
                super.bind(GLASS_SIZE, factor, magnification);
            }

            @Override
            protected double computeValue() {
                return GLASS_SIZE.get() * factor.get() / magnification.get();
            }
        };
        viewportSize.bind(db3);
        setupMagGlass();
        setupGlassGroup();
        setupDesc();
        bgImageView.requestFocus();
        primaryStage.setTitle("Magnifying Glass");
        primaryStage.setWidth(image.getWidth() / 2);
        primaryStage.setHeight(image.getHeight() / 2);


        root.getChildren().addAll(bgImageView, glassGroup, desc);

        primaryStage.setScene(scene);
        primaryStage.show();

        //style: StageStyle.UNDECORATED


    }

    public void adjustMagnification(final double amount) {
        DoubleProperty newMagnification = new SimpleDoubleProperty();
        DoubleBinding db3 = new DoubleBinding() {

            {
                super.bind(magnification);
            }

            @Override
            protected double computeValue() {
                if (magnification.get() + amount / 4 < .5) {
                    return .5;
                } else if (magnification.get() + amount / 4 > 10) {
                    return 10;
                } else {
                    return magnification.get() + amount / 4;
                }
            }
        };
        newMagnification.bind(db3);
        magnification.setValue(newMagnification.getValue());
    }

    private void setupGLASS_SIZE() {
        DoubleBinding db = new DoubleBinding() {

            {
                super.bind(bgImageView.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return bgImageView.boundsInLocalProperty().get().getWidth() / 4;
            }
        };
        GLASS_SIZE.bind(db);
        DoubleBinding db1 = new DoubleBinding() {

            {
                super.bind(GLASS_SIZE);
            }

            @Override
            protected double computeValue() {
                return GLASS_SIZE.get() / 2;
            }
        };
        GLASS_CENTER.bind(db1);
    }

    private void setupFactor() {
        DoubleBinding db = new DoubleBinding() {

            {
                super.bind(image.heightProperty(), bgImageView.boundsInLocalProperty());
            }

            @Override
            protected double computeValue() {
                return image.heightProperty().get() / bgImageView.boundsInLocalProperty().get().getHeight();
            }
        };

        factor.bind(db);

    }

    private void setupBgImageView() {
        bgImageView.setImage(image);
        bgImageView.fitWidthProperty().bind(scene.widthProperty());
        bgImageView.fitHeightProperty().bind(scene.heightProperty());

        BooleanBinding bb = new BooleanBinding() {

            {
                super.bind(factor);
            }

            @Override
            protected boolean computeValue() {
                if (factor.get() != 1.0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        bgImageView.cacheProperty().bind(bb);

        bgImageView.setSmooth(true);
        bgImageView.setPreserveRatio(true);
        bgImageView.setOnMouseMoved(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                centerX.setValue(me.getX());
                centerY.setValue(me.getY());
            }
        });
        bgImageView.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.EQUALS || ke.getCode() == KeyCode.PLUS) {
                    adjustMagnification(1.0);
                } else if (ke.getCode() == KeyCode.MINUS) {
                    adjustMagnification(-1.0);
                }
            }
        });


        bgImageView.setOnScroll(new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent me) {
            adjustMagnification(me.getDeltaY()/40);//40
        }
    });


        bgImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.PRIMARY) {
                    magGlass.setSmooth(magGlass.isSmooth());
                }
                bgImageView.requestFocus();
            }
        });


    }

    private void setupMagGlass() {
        magGlass.setImage(image);
        magGlass.setPreserveRatio(true);
        magGlass.fitWidthProperty().bind(GLASS_SIZE);
        magGlass.fitHeightProperty().bind(GLASS_SIZE);
        magGlass.setSmooth(true);
        ObjectBinding ob = new ObjectBinding() {

            {
                super.bind(viewportCenterX, viewportSize, viewportCenterY);
            }

            @Override
            protected Object computeValue() {
                return new Rectangle2D(viewportCenterX.get() - viewportSize.get() / 2, (viewportCenterY.get() - viewportSize.get() / 2), viewportSize.get(), viewportSize.get());
            }
        };
        magGlass.viewportProperty().bind(ob);
        Circle clip = new Circle();
        clip.centerXProperty().bind(GLASS_CENTER);
        clip.centerYProperty().bind(GLASS_CENTER);
        DoubleBinding db1 = new DoubleBinding() {

            {
                super.bind(GLASS_CENTER);
            }

            @Override
            protected double computeValue() {
                return GLASS_CENTER.get() - 5;
            }
        };
        clip.radiusProperty().bind(db1);
        magGlass.setClip(clip);

    }

    private void setupGlassGroup() {

        DoubleBinding db = new DoubleBinding() {

            {
                super.bind(centerX, GLASS_CENTER);
            }

            @Override
            protected double computeValue() {
                return centerX.get() - GLASS_CENTER.get();
            }
        };
        DoubleBinding db2 = new DoubleBinding() {

            {
                super.bind(centerY, GLASS_CENTER);
            }

            @Override
            protected double computeValue() {
                return centerY.get() - GLASS_CENTER.get();
            }
        };

        System.out.println("glassGroup.getLayoutX() " + glassGroup.getLayoutX());
        System.out.println("glassGroup.getLayoutY() " + glassGroup.getLayoutY());
        glassGroup.translateXProperty().bind(db);
        glassGroup.translateYProperty().bind(db2);

        Text text = new Text();
        DoubleBinding db3 = new DoubleBinding() {

            {
                super.bind(GLASS_CENTER);
            }

            @Override
            protected double computeValue() {
                return GLASS_CENTER.get() + GLASS_CENTER.get() / 2;
            }
        };
        text.xProperty().bind(db3);
        text.yProperty().bind(GLASS_SIZE);
        text.setText("x{%2.2f magnification}");
        Circle circle = new Circle();
        circle.centerXProperty().bind(GLASS_CENTER);
        circle.centerYProperty().bind(GLASS_CENTER);
        DoubleBinding db4 = new DoubleBinding() {

            {
                super.bind(GLASS_CENTER);
            }

            @Override
            protected double computeValue() {
                return GLASS_CENTER.get() - 2;
            }
        };
        circle.radiusProperty().bind(db4);
        circle.setStroke(Color.GREEN);
        circle.setStrokeWidth(3);
        circle.setFill(null);
        glassGroup.getChildren().addAll(magGlass, text, circle);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(4);
        glassGroup.setEffect(dropShadow);
        
        glassGroup.setMouseTransparent(true);

    }

    private void setupDesc() {
        desc.setX(10);
        desc.setY(15);
        if (!bgImageView.isFocused()) {
            desc.setText("Click image to focus");
        } else {
            desc.setText("Use the +/- or mouse wheel to zoom. Right-click to make the magnification "
                    + "{if (magGlass.smooth)   less smooth. else more smooth.}");
        }
        desc.setFont(new Font(12));
    }
}
