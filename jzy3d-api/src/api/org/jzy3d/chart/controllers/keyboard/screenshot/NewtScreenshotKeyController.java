package org.jzy3d.chart.controllers.keyboard.screenshot;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.AbstractController;

/** Saves a screenshot in PNG format once key S is pressed. 
 * 
 */
public class NewtScreenshotKeyController extends AbstractController implements KeyListener, IScreenshotKeyController {
    protected Chart chart;
    protected String outputFile;
    protected List<IScreenshotEventListener> listeners = new ArrayList<IScreenshotEventListener>(1);

    
    public NewtScreenshotKeyController(Chart chart, String outputFile) {
        super();
        register(chart);
        this.chart = chart;
        this.outputFile = outputFile;
    }
    
    public void register(Chart chart){
		super.register(chart);
        chart.getCanvas().addKeyController(this);
	}
	
	public void dispose(){
		for(Chart c: targets){
		    c.getCanvas().removeKeyController(this);
		}
		
		super.dispose(); // i.e. target=null
	}
    
    @Override
	public void screenshot(Chart chart, String filename) throws IOException {
        File output = new File(filename);
        if (!output.getParentFile().exists())
            output.mkdirs();
    	TextureIO.write(chart.screenshot(), new File(filename));  
    }
    
    @Override
	public void addListener(IScreenshotEventListener listener){
        listeners.add(listener);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 's':
            try {
                screenshot(chart, outputFile);
                fireDone(outputFile);
            } catch (IOException e1) {
                fireError(outputFile, e1);
            }
        default:
            break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    protected void fireDone(String file){
        for(IScreenshotEventListener listener: listeners){
            listener.doneScreenshot(file);
        }
    }
    
    
    protected void fireError(String file, Exception e){
        for(IScreenshotEventListener listener: listeners){
            listener.failedScreenshot(file, e);
        }
    }
}
