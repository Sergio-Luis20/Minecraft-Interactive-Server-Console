package br.sergio.mcsc.model.elements;

import br.sergio.mcsc.model.controls.ConsoleLabel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ConsoleComboBoxCellFactory implements Callback<ListView<ConsoleLabel>, ListCell<ConsoleLabel>> {
	
	@Override
	public ListCell<ConsoleLabel> call(ListView<ConsoleLabel> param) {
		ListCell<ConsoleLabel> listCell = new ListCell<ConsoleLabel>() {
			
			@Override
			protected void updateItem(ConsoleLabel item, boolean empty) {
				super.updateItem(item, empty);
				if(item == null || empty) {
					setGraphic(null);
				} else {
					setGraphic(new ConsoleLabel(item.getBundleText(), item.isBundle(), item.getURL()));
				}
			}
			
		};
		listCell.setStyle("-fx-background-color: #000000;");
		return listCell;
		
	}
	
}
