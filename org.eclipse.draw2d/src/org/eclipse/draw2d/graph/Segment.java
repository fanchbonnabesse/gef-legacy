/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.graph;

import org.eclipse.draw2d.geometry.Point;

/**
 * A Segment representation for the ShortestPathRouting. A segment is a line between
 * two vertices.
 * 
 * This class is for internal use only
 * @author Whitney Sorenson
 * @since 3.0
 */
class Segment {

Vertex start, end;

/**
 * Creates a segment between the given start and end points.
 * @param start the start vertex
 * @param end the end vertex
 */
Segment(Vertex start, Vertex end) {
	this.start = start;
	this.end = end;
}

/**
 * Returns the cosine of the made between this segment and the given segment
 * @param otherSegment the other segment
 * @return cosine value (not arc-cos)
 */
double cosine(Segment otherSegment) {
	double cos = (((start.x - end.x) * (otherSegment.end.x - otherSegment.start.x))
			+ ((start.y - end.y) * (otherSegment.end.y - otherSegment.start.y)))
				/ (getLength() * otherSegment.getLength());
	double sin = (((start.x - end.x) * (otherSegment.end.y - otherSegment.start.y))
			- ((start.y - end.y) * (otherSegment.end.x - otherSegment.start.x)));
	if (sin < 0.0)
		return (1 + cos);
		
	return -(1 + cos);
}

private long cross(int x1, int y1, int x2, int y2) {
	return x1 * y2 - x2 * y1;
}

/**
 * Returns the cross product of this segment and the given segment
 * @param otherSegment the other segment
 * @return the cross product
 */
long crossProduct(Segment otherSegment) {
	return (((start.x - end.x) * (otherSegment.end.y - end.y)) - ((start.y - end.y) * (otherSegment.end.x - end.x)));
}

private double getLength() {
	return (end.getDistance(start));
}

/**
 * Returns a number that represents the sign of the slope of this segment. It does 
 * not return the actual slope.
 * @return number representing sign of the slope
 */
double getSlope() {
	if (end.x - start.x >= 0) 
		return (end.y - start.y);
	else 
		return -(end.y - start.y);
}

/**
 * Returns true if the given segment intersects this segment.
 * @param sx start x
 * @param sy start y
 * @param tx end x
 * @param ty end y
 * @return true if the segments intersect
 */
boolean intersects(int sx, int sy, int tx, int ty) {
	/*
	 * Given the segments: u-------v. s-------t. If s->t is inside the
	 * triangle uvs, then check whether the line uv splits the line st.
	 */
	int su_x = start.x - sx;
	int su_y = start.y - sy;
	int sv_x = end.x - sx;
	int sv_y = end.y - sy;
	int st_x = sx - tx;
	int st_y = sy - ty;
	long product = cross(sv_x, sv_y, st_x, st_y)
			* cross(st_x, st_y, su_x, su_y);
	if (product >= 0) {
		int uvx = end.x - start.x;
		int uvy = end.y - start.y;
		int tux = start.x - tx;
		int tuy = start.y - ty;
		product = cross(-su_x, -su_y, uvx, uvy)
				* cross(uvx, uvy, tux, tuy);
		boolean intersects = product <= 0;
		return intersects;
	}
	return false;
}

/**
 * Return true if the segment represented by the points intersects this segment.
 * @param s start point
 * @param t end point
 * @return true if the segments intersect
 */
boolean intersects(Point s, Point t) {
	return intersects(s.x, s.y, t.x, t.y);
}

/**
 * Returns true if this segment intersects the given segment.
 * @param otherSegment the other segment
 * @return true if the segments intersect
 */
boolean intersects(Segment otherSegment) {
	return intersects(otherSegment.start.x, otherSegment.start.y, 
			otherSegment.end.x, otherSegment.end.y);
}

}