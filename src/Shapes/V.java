package Shapes;

import santa.v1.Engine;
import android.util.Pair;

public class V implements Shapes 
{
	@Override
	public NormShape calculateNorm() 
	{
		NormShape out = new NormShape(Engine.shape.V);
		float xMin = Float.MAX_VALUE, yMin = Float.MAX_VALUE;
		float xMax = 0, yMax = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{
			if(p.first < xMin) xMin = p.first;
			if(p.second < yMin ) yMin = p.second;
			if(p.first > xMax) xMax = p.first;
			if(p.second > yMax ) yMax = p.second;
		}
		if((yMax - yMin)/(xMax - xMin) < Engine.MIN_RATIO_FOR_VA) {out.norm = Float.MAX_VALUE; return out;}
		float xF = (xMin + xMax)/2f;
		float a1 = (yMin - yMax)/(xF - xMin);
		float a2 = (yMax - yMin)/(xMax - xF);
		out.norm = 0;
		for(Pair<Float, Float> p : Engine.pLine)
		{		
			if(p.first < xF)	
			{
				out.norm += (a1*(p.first - xMin) + (yMax - p.second))*(a1*(p.first - xMin) + (yMax - p.second));
			}
			else
			{
				out.norm += (a2*(p.first - xF) + (yMin - p.second))*(a2*(p.first - xF) + (yMin - p.second));
			}
		}
		
		out.norm = (float) Math.sqrt(out.norm)/Engine.pLine.size();
		return out;
	}

}
