package Shapes;

import santa.v1.Engine;

public class NormShape 
{
	public float norm = Float.MAX_VALUE;
	public Engine.shape shape;
	
	public NormShape() {}
	public NormShape(Engine.shape _shape)
	{
		shape = _shape;
	}
	public NormShape(NormShape ns)
	{
		norm = ns.norm;
		shape = ns.shape;
	}
}
