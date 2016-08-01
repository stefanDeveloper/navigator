package dhbw.navigator.controles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

import com.sun.rowset.internal.Row;
import dhbw.navigator.models.Edge;
import dhbw.navigator.models.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * MapControle
 * Control that can visualize a Node-Map with a Path
 * 
 * @author Stefan Machmeier, Manuela Leopold, Konrad Müller, Markus Menrath
 *
 */
public class MapControle extends StackPane {

	private ArrayList<Node> nodeList;
	private ArrayList<Node> pathList;
	private Node startNode;
	private Node destinationNode;

	private BorderPane parent = null;
	private double diameter = 10;
	private Canvas mapLayer = new Canvas();
	private Canvas pathLayer = new Canvas();
	private Canvas startMarkLayer = new Canvas();
	private Canvas destinatinMarkLayer = new Canvas();

	// upper left
	private double zoom = 1;
	private double xOffset = 0;
	private double yOffset = 0;
	private double xSize = 0;
	private double ySize = 0;
	private double lat1;
	private double lon1;
	private double xMulti;
	private double yMulti;

	public MapControle()
	{
		//Add mapLayer (map) and pathLayer (path)
		getChildren().addAll(mapLayer,
				startMarkLayer,
				destinatinMarkLayer,
				pathLayer
		);
		//Set default values for the layer
		pathLayer.getGraphicsContext2D().setStroke(Color.AQUA);
		pathLayer.getGraphicsContext2D().setLineWidth(3);

		startMarkLayer.getGraphicsContext2D().setFill(Color.GREEN);
		destinatinMarkLayer.getGraphicsContext2D().setFill(Color.RED);


		//Listen to a change of the parent object
		this.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> ov, Parent oldP, Parent newP) {
				if (newP != null) {
					//Set new parent
					MapControle.this.parent = (BorderPane)getParent().getParent();
					
					//Update the graph
					updateGraph();
					//Listen to the height and width of the parent
					//and redraw the map
					parent.heightProperty().addListener(new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> observable, Number oldValue,
											Number newValue){
							updateGraph();
						}
					});
					parent.widthProperty().addListener(new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> observable, Number oldValue,
											Number newValue) {
							updateGraph();
						}
					});
				}
			}
		});
	}



	private int movement = 25;

	public void moveLeft(){
		xOffset += movement;
		updateGraph();
	}

	public void moveRight(){
		xOffset -= movement;
		updateGraph();
	}

	public void moveDown(){
		yOffset -= movement;
		updateGraph();
	}

	public void moveUp(){
		yOffset += movement;
		updateGraph();
	}

	public void zoomIn(){
		zoom += 0.1;
		updateGraph();
	}

	public void zoomOut(){
		zoom -= 0.1;
		updateGraph();
	}
	
	public void setPath(ArrayList<Node> pPathList){
		pathList = pPathList;
		updateGraph();
	}

	public void setOriginMap(ArrayList<Node> pNodeList) {
		nodeList = pNodeList;
		updateGraph();
	}

	public void setStart(Node n){
		startNode = n;
		updateGraph();
	}

	public void setDestinationNode(Node n){
		destinationNode = n;
		updateGraph();
	}
	
	//Update the graph
	//Calculate how to scale the map
	void updateGraph() {

		double lon2;
		double lat2;

		if (this.parent != null && nodeList!=null) {
			this.xSize = this.parent.getWidth();
			this.ySize = this.parent.getHeight();
			if (this.xSize < this.ySize) {
				this.ySize = this.xSize;
			} else {
				this.xSize = this.ySize;
			}

			Node firstNode = this.nodeList.get(0);


			lat1 = firstNode.getLat().doubleValue();
			lon1 = firstNode.getLon().doubleValue();
			// lower right

			lon2 = firstNode.getLon().doubleValue();

			lat2 = firstNode.getLat().doubleValue();

			double compareLat;
			double compareLon;
			//Get the highest left and lowest right coordinate
			for (Node n : this.nodeList) {
				compareLat = n.getLat().doubleValue();
				compareLon = n.getLon().doubleValue();
				if (compareLat < lat1)
					lat1 = compareLat; // compare is smaller
				if (compareLon> lon1)
					lon1 = compareLon; // compare is smaller

				if (compareLat > lat2)
					lat2 = compareLat; // Compare is greater
				if (compareLon < lon2)
					lon2 = compareLon; // compare is greater
			}
			//Calculate the relation between X and Y
			//and calculate the strechting factor to fit the window
			double latDif = lat2 - lat1; // Breitenunterschied
			double lonDif = lon2 - lon1; // Höhenunterschied


			xMulti = -(-this.xSize / lonDif);
			yMulti = -(this.ySize / latDif);

			double multi = 0;
			//Make sure that the drawing won't overflow the screen
			if (xMulti < yMulti) {
				multi = xMulti;
			}

			xMulti = xMulti * zoom;
			yMulti = yMulti * zoom;

			clearAndResizeAllLayer(mapLayer, pathLayer, startMarkLayer, destinatinMarkLayer);
			drawLines(nodeList,
					mapLayer,
					false);

			if(pathList != null)
			{
				drawLines(pathList,
						pathLayer,
						true);
			}

			drawPoint(startNode, startMarkLayer);
			drawPoint(destinationNode, destinatinMarkLayer);
		}
	}

	void clearAndResizeAllLayer(Canvas ... l){
		for(Canvas c: l){
			GraphicsContext graphic = c.getGraphicsContext2D();
			graphic.getCanvas().setHeight(this.ySize);
			graphic.getCanvas().setWidth(this.xSize);
			graphic.clearRect(0, 0, xSize, ySize);
		}
	}

	void drawPoint(Node n,
				   Canvas layer)
	{
		if(n!=null){
			double highlightDiameter = diameter * 1.5;
			double nLon = calculateX(n.getLon().doubleValue());
			double nLat = calculateY(n.getLat().doubleValue());
			layer.getGraphicsContext2D().fillOval(nLon - highlightDiameter / 2,
					nLat - highlightDiameter / 2, highlightDiameter,
					highlightDiameter);
		}
	};
	
	//Draw the lines on the canvas
	void drawLines(ArrayList<Node> nodes,
				   Canvas layer,
				   Boolean highlightPath)
	{
		//Set base settings for the canvas
		GraphicsContext graphic = layer.getGraphicsContext2D();
		
		//Draw all edges
		for (Node n : nodes) {
			double nLon = calculateX(n.getLon().doubleValue());
			double nLat = calculateY(n.getLat().doubleValue());

			if(!highlightPath && -xMulti > 550){
				if(n.getJunctionsCount()>2||-xMulti > 450) //graphic.fillText(n.getName(), nLon + 15, nLat);
				graphic.fillOval(nLon - diameter / 2,
						nLat - diameter / 2,
						diameter,
						diameter);
				graphic.fillText(n.getName() + " "+n.getShortestDistance(), nLon + 15, nLat);
			}


			//Draw each path of the edge
			for (Edge e : n.getEdges()) {
				ArrayList<Node> allNodes = e.allNodes();

				boolean drawPath = !highlightPath;
				if(highlightPath && (
						(e.getEndNode() == n && nodes.contains(e.getStartNode())
						||(e.getStartNode() == n && nodes.contains(e.getEndNode())
						)
						))){
					drawPath = true;
				}

				if(drawPath){
					for (int i = 0; i < allNodes.size() - 1; i++) {
						Node en = allNodes.get(i);
						Node enNext = allNodes.get(i + 1);
						double lo1 = calculateX(en.getLon().doubleValue());
						double la1 = calculateY(en.getLat().doubleValue());
						double lo2 = calculateX(enNext.getLon().doubleValue());
						double la2 = calculateY(enNext.getLat().doubleValue());
						graphic.strokeLine(lo2, la2, lo1, la1);
					}
				}
			}
		}
	}

	double calculateX(double input){
		return (lon1 -input) * xMulti + this.xSize + xOffset;
	}

	double calculateY(double input){
		return (input - lat1) * yMulti + this.ySize + yOffset;
	}
}
