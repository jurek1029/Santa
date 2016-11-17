package Shapes;

import santa.v1.Engine;
import android.util.Pair;

public class LineH implements Shapes 
{

	@Override
	public NormShape calculateNorm() 
	{
		NormShape out = new NormShape(Engine.shape.lineH);
		float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
		float yMax = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{
			if(p.first < xMin) xMin = p.first;
			if(p.second < yMin ) yMin = p.second;
			if(p.second > yMax) yMax = p.second;
		}		
		float yF = (yMin + yMax)/2f;
		out.norm = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{			
			out.norm += (p.second - yF)*(p.second - yF);			
		}
		out.norm = (float) Math.sqrt(out.norm)/Engine.pLine.size();
		return out;
	}

}
