package Controller;

import Model.Slide;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

public class SlideViewController {
    @FXML
    private ImageView imageView;
    @FXML
    private TextArea textArea;

    private Slide currentSlide;

    public void setSlide(Slide slide) {
        this.currentSlide = slide;
        updateView();
    }

    private void updateView() {
        if (currentSlide != null) {
            imageView.setImage(currentSlide.getImage());
            textArea.setText(currentSlide.getContent());
        }
    }
}
