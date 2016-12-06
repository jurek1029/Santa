package santa.v1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MenuView extends GLSurfaceView
{

	public MenuView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setRenderer(new MenuRenderer());
	}

	@Override
	public void onResume()
	{
		super.onResume();		
	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
}
