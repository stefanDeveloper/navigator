package dhbw.navigator.controles;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.SortedSet;

public class AutoCompleteControle extends GridPane {
	private Button clearButton;
	private TextFieldAutoCompleteControle textField;
	private PropertyChangeSupport changes = new PropertyChangeSupport( this );
	private String text;

	public AutoCompleteControle(String labelText) {
		Label label = new Label(labelText);
		clearButton = new Button("X");
		textField = new TextFieldAutoCompleteControle();
		clearButton.setVisible(false);
		setPadding(new Insets(5,0,0,5));


		for (int i = 0; i < 4; i++) {
			ColumnConstraints col = new ColumnConstraints();
			getColumnConstraints().add(col);
		}

		getColumnConstraints().get(0).setPercentWidth(15);
		getColumnConstraints().get(1).setPercentWidth(70);
		getColumnConstraints().get(2).setPercentWidth(2.5);
		getColumnConstraints().get(3).setPercentWidth(12.5);

		add(label, 0, 0);
		add(textField, 1 , 0);
		add(clearButton, 3, 0);

		this.clearButton.setOnAction(event -> AutoCompleteControle.this.textField.setText(""));
		this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (AutoCompleteControle.this.textField.getText().length() > 0
                    && !(AutoCompleteControle.this.textField.getText().equals(" "))) {
                AutoCompleteControle.this.clearButton.setVisible(true);

            } else if (AutoCompleteControle.this.textField.getText().length() == 0) {
                AutoCompleteControle.this.clearButton.setVisible(false);
            }
            changes.firePropertyChange("text", oldValue, newValue);
        });
	}

	public void setNamesOfJunctions(SortedSet<String> namesOfJunctions) {
		textField.setContext(namesOfJunctions);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
