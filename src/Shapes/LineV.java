package Shapes;

import java.util.Vector;

import android.util.Pair;
import santa.v1.Engine;

public class LineV implements Shapes 
{

	@Override
	public NormShape calculateNorm() 
	{
		NormShape out = new NormShape(Engine.shape.lineV);
		float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
		float xMax = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{
			if(p.first < xMin) xMin = p.first;
			if(p.second < yMin ) yMin = p.second;
			if(p.first > xMax) xMax = p.first;
		}
		float xF = (xMin + xMax)/2f;
		out.norm = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{			
			out.norm += (p.first - xF)*(p.first - xF);			
		}
		out.norm = (float) Math.sqrt(out.norm)/Engine.pLine.size();
		return out;
	}
}

