package orchestration.path;

import java.awt.Point;
import java.awt.Polygon;

import org.apache.commons.math.geometry.Vector3D;

public class RectShape implements PlannerShape, Cloneable
{
	private Polygon poly;
	private Point centerPt;
	private float headingDeg = 90.f;
	
	public RectShape(float width, float height)
	{
		centerPt = new Point(0,0);
		poly = new Polygon(
				new int[]{
						 (int)width / 2,
						-(int)width / 2,
						-(int)width / 2,
						 (int)width / 2
				}, 
				new int[]{
						 (int)height / 2,
						 (int)height / 2,
						-(int)height / 2,
						-(int)height / 2
				}, 4);
	}
	
	public static PlannerShape easy(float width, float height, lejos.geom.Point location, double heading)
	{
		RectShape ret = new RectShape(width, height);
		return ret.rotateTo((float)heading).moveTo(new Point((int)location.x, (int)location.y));
	}
	
	@Override
	public PlannerShape rotateToward(Point pt)
	{
		float o = pt.y - centerPt.y;
		float a = pt.x - centerPt.x;
		
		double angleRad = Math.atan2(o, a); 
		float angleDeg = (float)(180. * angleRad / Math.PI);
		
		return rotateTo(angleDeg);
	}

	@Override
	public PlannerShape rotateBy(float degrees)
	{
		RectShape copy = null;
		try
		{
			copy = (RectShape)this.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		float radians = degrees * (float)Math.PI / 180.f;
		
		for(int i = 0; i < poly.npoints; i++)
		{
			Point oldPt = new Point(
				copy.poly.xpoints[i],
				copy.poly.ypoints[i]);
			
			Point newPt = rotatePoint(oldPt, centerPt, radians);
			copy.poly.xpoints[i] = newPt.x;
			copy.poly.ypoints[i] = newPt.y;
		}
		
		copy.poly.invalidate();
		copy.headingDeg = headingDeg + degrees;
		
		return copy;
	}

	@Override
	public PlannerShape rotateTo(float degrees)
	{
		return rotateBy(degrees - headingDeg);
	}

	// Formula obtained here: http://www.openprocessing.org/visuals/?visualID=6782
	private static Point rotatePoint(Point pt, Point pivot, float theta)
	{
		Point rot = new Point();
		rot.x = (int)(pivot.x + (pt.x - pivot.x) * Math.cos(theta) - (pt.y - pivot.y) * Math.sin(theta));

		rot.y = (int)(pivot.y + (pt.x - pivot.x) * Math.sin(theta) + (pt.y - pivot.y) * Math.cos(theta));
		
		return rot;
	}
	
	@Override
	public PlannerShape moveTo(Point pt)
	{	
		RectShape copy = null;
		try
		{
			copy = (RectShape)this.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		copy.poly.translate(pt.x - centerPt.x, pt.y - centerPt.y);
		copy.centerPt = pt;
		
		return copy;
	}

	@Override
	public Polygon getPolygon()
	{
		return poly;
	}

	@Override
	public boolean collidesWith(PlannerShape shape)
	{
		Polygon otherPoly = shape.getPolygon();
		
		// quick collision check
		if (!otherPoly.intersects(poly.getBounds())) return false;
		

		// check all line segments for intersection
		for(int i = 0; i < poly.npoints; i++)
		{
			Vector3D p = createPoint(poly.xpoints[i], poly.ypoints[i]);
			Vector3D r = createLine(p, poly.xpoints[(i+i)%poly.npoints], poly.xpoints[(i+i)%poly.npoints]);
			
			for (int j = 0; j < otherPoly.npoints; j++)
			{
				Vector3D q = createPoint(otherPoly.xpoints[i], otherPoly.ypoints[i]);
				Vector3D s = createLine(q, otherPoly.xpoints[(i+i)%otherPoly.npoints], otherPoly.xpoints[(i+i)%otherPoly.npoints]);
				
				if (testIntersection (p, r, q, s)) return true;
			}
		}
		
		// check if one polygon completely contains the other
		if (otherPoly.contains(poly.xpoints[0], poly.ypoints[0])) return true;
		else if (poly.contains(otherPoly.xpoints[0], otherPoly.ypoints[0])) return true;
		
		return false;
	}
	
	private static Vector3D createPoint(int x, int y)
	{
		return new Vector3D((double) x, (double) y, 0.);
	}
	
	private static Vector3D createLine(Vector3D p, int x2, int y2)
	{
		return new Vector3D((double) x2, (double) y2, 0.).subtract(p);
	}
	
	//http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	//t = (q − p) × s / (r × s)
	//u = (q − p) × r / (r × s)
	private static boolean testIntersection(
			Vector3D p, Vector3D r, 
			Vector3D q, Vector3D s)
	{
		double bottomFactor = Vector3D.crossProduct(r, s).getNorm();
		if (bottomFactor == 0) 
			return false; // parallel
		else
		{
			double t = Vector3D.crossProduct((q.subtract(p)), s).getNorm() / bottomFactor;
			double u = Vector3D.crossProduct((q.subtract(p)), r).getNorm() / bottomFactor;
			if (0 <= t && t <= 1 && 0 <= u && u <= 1) return true;
			else return false;
		}
	}

	@Override
	public Point center()
	{
		return centerPt;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		RectShape clone = (RectShape) super.clone();
		
		clone.poly = new Polygon(
				clone.poly.xpoints.clone(), 
				clone.poly.ypoints.clone(),
				clone.poly.npoints);
		
		clone.centerPt = new Point(clone.centerPt.x, clone.centerPt.y);
		return clone;
	}
}
