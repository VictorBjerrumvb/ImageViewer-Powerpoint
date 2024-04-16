package Controller;

import Model.Presentation;
import Model.Slide;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainViewController {
    public Button loadImagesButton;
    public Button startStopSlideshowButton;
    public Button previousButton;
    public Button nextButton;
    // FXML-annoterede felter knytter sig til komponenter defineret i FXML-filen.
    @FXML
    private ImageView imageView; // Komponent til at vise billeder.
    @FXML
    private Label titleLabel, contentLabel; // Labels til at vise titel og indhold.

    private Presentation presentation; // Modelobjekt der indeholder alle slides.
    private int currentIndex = 0; // Holder styr på, hvilket slide der aktuelt vises.
    private Timeline slideshowTimeline; // Tidsplan for at automatisere slideshowet.

    // Initialize kaldes efter, at alle @FXML-felter er injiceret.
    public void initialize() {
        presentation = new Presentation(); // Initialiserer presentation.
        slideshowTimeline = new Timeline(new KeyFrame(Duration.seconds(2), ae -> onNext(null)));
        slideshowTimeline.setCycleCount(Timeline.INDEFINITE); // Gør at slideshowet kører uendeligt.
        startStopSlideshowButton.setText("Start Slideshow"); // Sætter startteksten for knappen
    }


    // Metode til at indlæse slides fra en mappe valgt af brugeren.
    private void loadSlidesIntoPresentation(File directory) {
        presentation.getSlides().clear(); // Fjerner eksisterende slides.
        try {
            // Går gennem alle filer i den valgte mappe og tilføjer dem som slides.
            Files.walk(Paths.get(directory.toURI()))
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().matches(".*\\.(jpg|png|jpeg)$")) // Filtrer kun billede filer.
                    .forEach(filePath -> {
                        String fileName = filePath.getFileName().toString();
                        presentation.getSlides().add(new Slide("Title for " + fileName, "Content for " + fileName, filePath.toString()));
                    });
        } catch (Exception e) {
            e.printStackTrace(); // Printer fejl hvis der opstår problemer under indlæsningen.
        }
    }

    // Handler for "Previous" knappen. Viser det forrige slide.
    @FXML
    protected void onPrevious(ActionEvent event) {
        if (currentIndex == 0) {
            currentIndex = presentation.getSlides().size() - 1; // Går til det sidste slide hvis vi er på det første.
        } else {
            currentIndex--; // Går et slide tilbage.
        }
        updateView(); // Opdaterer visningen.
    }

    // Handler for "Next" knappen. Viser det næste slide.
    @FXML
    protected void onNext(ActionEvent event) {
        if (currentIndex == presentation.getSlides().size() - 1) {
            currentIndex = 0; // Går tilbage til det første slide hvis vi er på det sidste.
        } else {
            currentIndex++; // Går et slide frem.
        }
        updateView(); // Opdaterer visningen.
    }

    // Opdaterer UI-komponenterne med information fra det aktuelle slide.
    private void updateView() {
        Slide currentSlide = presentation.getSlides().get(currentIndex);
        imageView.setImage(currentSlide.getImage());
        titleLabel.setText(currentSlide.getTitle());
        contentLabel.setText(currentSlide.getContent());
        System.out.println("Viser slide: " + currentIndex); // Til debugging

        countColorsAndDisplay(currentSlide); // Kommenter denne ud midlertidigt hvis den ikke er implementeret endnu
    }

    // Åbner en mappevælger så brugeren kan vælge en mappe med billeder.
    public void onLoadImages(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Image Directory");
        File directory = directoryChooser.showDialog(null); // Viser mappens dialogvindue.

        if (directory != null) {
            loadSlidesIntoPresentation(directory); // Indlæser slides fra den valgte mappe.
            System.out.println(presentation.getSlides().size() + " slides loaded.");// Tilføj denne linje for at bekræfte antallet af indlæste slides
            if (!presentation.getSlides().isEmpty()) {
                currentIndex = 0; // Nulstiller til det første slide.
                updateView(); // Opdaterer visningen.
            }
        }
    }

    // Starter eller stopper slideshowet baseret på dets nuværende tilstand.
    public void onStartStopSlideshow(ActionEvent event) {
        if (slideshowTimeline.getStatus() == Animation.Status.RUNNING) {
            slideshowTimeline.stop();
            startStopSlideshowButton.setText("Start Slideshow"); // Ændrer teksten til "Start Slideshow"
            System.out.println("Slideshow stopped."); // Tilføj for debugging
        } else {
            slideshowTimeline.play();
            startStopSlideshowButton.setText("Stop Slideshow"); // Ændrer teksten til "Stop Slideshow"
            System.out.println("Slideshow started."); // Tilføj for debugging
        }
    }

    // Metode til at tælle farver i billedet (endnu ikke implementeret).
    private void countColorsAndDisplay(Slide slide) {
        new Thread(() -> {
            try {
                // Få adgang til billedet fra sliden
                Image image = slide.getImage();
                // Opret en PixelReader
                PixelReader pixelReader = image.getPixelReader();
                if (pixelReader == null) {
                    throw new IllegalStateException("Billedet kan ikke læses");
                }

                int redCount = 0;
                int greenCount = 0;
                int blueCount = 0;

                // Dimensionerne af billedet
                int width = (int) image.getWidth();
                int height = (int) image.getHeight();

                // Analyser hver pixel
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        Color color = pixelReader.getColor(x, y);
                        // Tjek den dominerende farve
                        if (color.getRed() > color.getGreen() && color.getRed() > color.getBlue()) {
                            redCount++;
                        } else if (color.getGreen() > color.getRed() && color.getGreen() > color.getBlue()) {
                            greenCount++;
                        } else if (color.getBlue() > color.getRed() && color.getBlue() > color.getGreen()) {
                            blueCount++;
                        }
                    }
                }

                // Opdater UI med farvetællinger
                final int finalRedCount = redCount;
                final int finalGreenCount = greenCount;
                final int finalBlueCount = blueCount;
                Platform.runLater(() -> {
                    contentLabel.setText("Rød: " + finalRedCount + ", Grøn: " + finalGreenCount + ", Blå: " + finalBlueCount);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
