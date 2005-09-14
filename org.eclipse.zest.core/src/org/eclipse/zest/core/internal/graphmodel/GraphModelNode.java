/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.mylar.zest.core.internal.graphmodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.mylar.zest.core.ZestColors;
import org.eclipse.mylar.zest.layouts.LayoutEntity;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


/**
 * Simple node class which has the following properties: color, size, location, and a label.
 * It also has a list of connections and anchors.
 *  
 * @author Chris Callendar
 */
public class GraphModelNode extends GraphItem implements LayoutEntity {

	public static final String LOCATION_PROP = "GraphModelNode.Location";
	public static final String SIZE_PROP = "GraphModelNode.Size";
	public static final String COLOR_BG_PROP = "GraphModelNode.BGColor";
	public static final String COLOR_FG_PROP = "GraphModelNode.FGColor";
	public static final String HIGHLIGHT_PROP = "GraphModeNode.Highlight";
	public static final String UNHIGHLIGHT_PROP = "GraphModeNode.Unhighlight";
	public static final String SOURCE_CONNECTIONS_PROP = "GraphModelNode.SourceConn";
	public static final String TARGET_CONNECTIONS_PROP = "GraphModelNode.TargetConn";

	private List sourceConnections;
	private List targetConnections;
	
	private boolean preferredLocation;

	private Color foreColor;
	private Color backColor;
	private Color highlightColor;
	private Color highlightAdjacentColor;
	private Color unhighlightColor;
	private boolean highlightAdjacentNodes;
	private Color borderColor;
	private Color borderHighlightColor;
	private Color borderUnhighlightColor;
	private int borderWidth;
	private Point location;
	private Dimension size;
	private Font font;
	private EditPart editPart;
	protected Dimension labelSize;
	
	protected GraphModel graphModel;
	
	/** The internal node. */
	protected Object internalNode;
	
	
	public GraphModelNode(GraphModel graphModel, Object externalNode) {
		super(graphModel);
		initModel(graphModel, externalNode);
	}
	
	public GraphModelNode(GraphModel graphModel, String label, Object externalNode) {
		super(graphModel);
		setText(label);
		initModel(graphModel, externalNode);
	}

	public GraphModelNode(GraphModel graphModel, Image i, Object externalNode) {
		super(graphModel);
		setImage(i);
		initModel(graphModel, externalNode);
	}
	
	public GraphModelNode(GraphModel graphModel, String label, Image i, Object externalNode) {
		super(graphModel);
		setText(label);
		setImage(i);
		initModel(graphModel, externalNode);
	}
	
	protected void initModel(GraphModel graphModel, Object externalNode) {		
		this.setData(externalNode);
		this.sourceConnections = new ArrayList();
		this.targetConnections = new ArrayList();
		this.preferredLocation = false;		
		this.foreColor = ColorConstants.black;
		this.backColor = ZestColors.LIGHT_BLUE;
		this.highlightColor = ColorConstants.yellow;
		this.unhighlightColor = this.backColor;
		this.highlightAdjacentColor = ColorConstants.orange;
		this.highlightAdjacentNodes = true;
		this.borderColor = ColorConstants.black;
		this.borderHighlightColor = ColorConstants.blue;
		this.borderUnhighlightColor = ColorConstants.black;
		this.borderWidth = 1;
		this.location = new Point(10, 10);
		this.size = new Dimension(20, 20);
		this.font = Display.getDefault().getSystemFont();
		this.graphModel = graphModel;
	}
	
	public void setEditPart( EditPart editPart ) {
		this.editPart = editPart;
	}
	
	public EditPart getEditPart() {
		return this.editPart;
	}
	
	public String toString() {
		return "GraphModelNode: " + getText();
	}

	public int compareTo(Object otherNode) {
		int rv = 0;
		if (otherNode instanceof GraphModelNode) {
			GraphModelNode node = (GraphModelNode)otherNode;
			if (this.getText() != null) {
				rv = this.getText().compareTo(node.getText());
			}
		}
		return rv;
	}

	/**
	 * Gets the user data associated with this node.
	 * @return The user data associated with this node
	 */
	public Object getExternalNode() {
		return this.getData();
	}
	
	/**
	 * Returns a new list of the source connections.
	 * @return List a new list
	 */
	public List getSourceConnections() {
	  	return new ArrayList(sourceConnections);
	}

	/**
	 * Returns a new list of the target connections.
	 * @return List a new list
	 */
	public List getTargetConnections() {
	  	return new ArrayList(targetConnections);
	}
	

	/**
	 * Adds the given connection to the list of connections.
	 * @param connection
	 */
	public void addConnection(GraphModelConnection connection) {
		if (connection != null) {
			if (connection.getSource() == this) {
				sourceConnections.add(connection);
				firePropertyChange(SOURCE_CONNECTIONS_PROP, null, connection);
			} else if (connection.getDestination() == this) {
				targetConnections.add(connection);
				firePropertyChange(TARGET_CONNECTIONS_PROP, null, connection);
			}
		}
	}
	
	/**
	 * Removes the connection from the list if it exists.
	 * @param connection
	 * @return boolean if the connection was removed
	 */
	public boolean removeConnection(GraphModelConnection connection) {
		boolean removed = false;
		if (connection != null) {
			if (connection.getSource() == this) {
				removed = sourceConnections.remove(connection);
				if (removed) {
					firePropertyChange(SOURCE_CONNECTIONS_PROP, null, connection);
				}
			}
			else if (connection.getDestination() == this) {
				removed = targetConnections.remove(connection);
				if (removed) {
					firePropertyChange(TARGET_CONNECTIONS_PROP, null, connection);
				}
			}
		}
		return removed;
	}
	
	public void setHasPreferredLocation( boolean preferredLocation ) {
		this.preferredLocation = preferredLocation;
	}
	
	public boolean hasPreferredLocation() {
		return preferredLocation;
	}

	public double getXInLayout() {
		return location.x;
	}

	public double getYInLayout() {
		return location.y;
	}
	
	/**
	 * Returns the bounds of this node.  It is just the combination
	 * of the location and the size.
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return new Rectangle(getLocation(), getSize());
	}
	
	/**
	 * Returns a copy of the node's location.
	 * @return Point
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	public double getWidthInLayout() {
		return size.width;
	}

	public double getHeightInLayout() {
		return size.height;
	}
	
	public void setSelected( boolean selected ) {
		if (selected) {
			firePropertyChange( HIGHLIGHT_PROP, null, null );
		} else {
			firePropertyChange( UNHIGHLIGHT_PROP, null, null );
		}
	}
	
	public void setPreferredLocation( double x, double y ) {
		location.setLocation((int)x, (int)y);
		//DebugPrint.println("Called Set Location: " + x + " : " + y);
		firePropertyChange(LOCATION_PROP, null, location);
	}
	
	public void setLocationInLayout(double x, double y) {
		if (!preferredLocation) {
			if ((x != location.x) || (y != location.y)) {
				location.setLocation((int)x, (int)y);
				//DebugPrint.println("Called Set Location: " + x + " : " + y);
				firePropertyChange(LOCATION_PROP, null, location);
			}
		}
	}
	
	/**
	 * Updates the location <b>WITHOUT</b> firing a property change.
	 */
	public void updateLocationInLayout(double x, double y) {
		location.setLocation((int)x, (int)y);
	}
	
	/**
	 * Returns a copy of the node's size.
	 * @return Dimension
	 */
	public Dimension getSize() {
		return size.getCopy();
	}
	
	public void setSizeInLayout(double width, double height) {
		if ((width != size.width) || (height != size.height)) {
			size.width = (int)width;
			size.height = (int)height;
			firePropertyChange(SIZE_PROP, null, size);
		}
	}
	
	public Color getForegroundColor() {
		return foreColor;
	}
	
	public void setForegroundColor(Color c) {
		Color old = foreColor;
		this.foreColor = c;
		firePropertyChange(COLOR_FG_PROP, old, c);
	}
	
	public Color getBackgroundColor() {
		return backColor;
	}
	
	/**
	 * Permantly sets the background color (unhighlighted).
	 * For temporary color changes call #changeBackgroundColor instead.
	 * @param c
	 */
	public void setBackgroundColor(Color c) {
		unhighlightColor = c;
		changeBackgroundColor(c);
	}
	
	/**
	 * Changes the background color and fires a property change event.
	 * @param c
	 */
	protected void changeBackgroundColor(Color c) {
		Color old = backColor;
		backColor = c;
		firePropertyChange(COLOR_BG_PROP, old, c);
		
	}
	
	public Color getHighlightColor() {
		return highlightColor;
	}
	
	public void setHighlightColor(Color c) {
		this.highlightColor = c;
	}
	
	public Color getHighlightAdjacentColor() {
		return highlightAdjacentColor;
	}
	
	public void setHighlightAdjacentColor(Color c) {
		this.highlightAdjacentColor = c;
	}	
	/**
	 * Highlights the node changing the background color and border color.
	 * The source and destination connections are also highlighted,
	 * and the adjacent nodes are highlighted too in a different color.
	 */
	public void highlight() {
		if (backColor != highlightColor) {
			borderColor = borderHighlightColor;
			changeBackgroundColor(highlightColor);
			// highlight the adjacent edges & nodes
			for (Iterator iter = sourceConnections.iterator(); iter.hasNext();) {
				GraphModelConnection conn = (GraphModelConnection)iter.next();
				conn.highlight();
				conn.getDestination().highlightAdjacent();
			}
			for (Iterator iter = targetConnections.iterator(); iter.hasNext();) {
				GraphModelConnection conn = (GraphModelConnection)iter.next();
				conn.highlight();
				conn.getSource().highlightAdjacent();
			}
		}
	}
	
	/**
	 * Restores the nodes original background color and border width.
	 */
	public void unhighlight() {
		if (unhighlightColor != backColor) {
			changeBackgroundColor(unhighlightColor);
			borderColor = borderUnhighlightColor;
			// unhighlight the adjacent edges
			for (Iterator iter = sourceConnections.iterator(); iter.hasNext();) {
				GraphModelConnection conn = (GraphModelConnection)iter.next();
				conn.unhighlight();
				conn.getDestination().unhighlight();
			}
			for (Iterator iter = targetConnections.iterator(); iter.hasNext();) {
				GraphModelConnection conn = (GraphModelConnection)iter.next();
				conn.unhighlight();
				conn.getSource().unhighlight();
			}
		}
	}
	
	/**
	 * Highlights this node using the adjacent highlight color.
	 * This only does something if highlighAdjacentNodes is set to true 
	 * and if the node isn't already highlighted.
	 * @see #setHighlightAdjacentNodes(boolean)
	 */
	public void highlightAdjacent() {
		if (highlightAdjacentNodes && (backColor != highlightAdjacentColor) && (backColor != highlightColor)) {
			borderColor = borderHighlightColor;
			changeBackgroundColor(highlightAdjacentColor);
		}
	}
	
	/**
	 * Returns if the nodes adjacent to this node will be highlighted when
	 * this node is selected.
	 * @return GraphModelNode
	 */
	public boolean isHighlightAdjacentNodes() {
		return highlightAdjacentNodes;
	}

	/**
	 * Sets if the adjacent nodes to this one should be highlighted when
	 * this node is selected.
	 * @param highlightAdjacentNodes The highlightAdjacentNodes to set.
	 */
	public void setHighlightAdjacentNodes(boolean highlightAdjacentNodes) {
		this.highlightAdjacentNodes = highlightAdjacentNodes;
	}
	
	public Color getBorderColor() {
		return borderColor;
	}
	
	public int getBorderWidth() {
		return borderWidth;
	}

	public Object getLayoutInformation() {
		return internalNode;
	}

	public void setLayoutInformation(Object layoutInformation) {
		this.internalNode = layoutInformation;
	}

	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		this.labelSize = null;
		this.font = font;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Item#setText(java.lang.String)
	 */
	public void setText(String string) {
		this.labelSize = null;
		super.setText(string);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Item#setImage(org.eclipse.swt.graphics.Image)
	 */
	public void setImage(Image image) {
		this.labelSize = null;
		super.setImage(image);
	}
	
	/**
	 * Returns the extent of the text and the image with some padding.
	 * @return Dimension the minimum size needed to display the text and the image
	 */
	public Dimension calculateMinimumLabelSize() {
		if (labelSize == null) {
			Dimension text = calculateTextExtents();
			Dimension icon = calculateImageExtents();
			labelSize = new Dimension(text.width + icon.width, Math.max(text.height, icon.height));
			labelSize.expand(12, 6);
		}
		return labelSize;
	}
	
	/**
	 * Gets the minimum size for this node.  This is the minimum size of the label (text & icon)
	 * @return Dimension
	 */
	public Dimension calculateMinimumSize() {
		return calculateMinimumLabelSize();
	}
	
	
	/**
	 * Gets the graphModel that this node is contained in
	 * @return The graph model that this node is contained in
	 */
	public GraphModel getGraphModel() {
		return this.graphModel;
	}
	
	
	private Dimension calculateTextExtents() {
		Dimension dim = new Dimension(0, 0);
		String text = getText();
		if (text != null) {
			dim.setSize(FigureUtilities.getTextExtents(text + "  ", getFont()));
		}
		return dim;
	}
	
	private Dimension calculateImageExtents() {
		Dimension dim = new Dimension(0, 0);
		Image image = getImage();
		if (image != null) {
			dim.setSize(new Dimension(image.getBounds().width + 4, image.getBounds().height));
		}
		return dim;
	}
	
	
	
}