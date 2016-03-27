import java.io.IOException;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.HandList.HandListIterator;

class LeapListner extends Listener {
	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}
	
	public void onConnect(Controller controller){
		System.out.println("Connected to Leap Motion Sensor");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		
	}
	
	public void onDisconnect(Controller controller) {
		System.out.println("Disconnected Leap Motion Sensor");
	}
	
	public void onExit(Controller controller){
		System.out.println("Exited");
	}
	
	public void onFrame(Controller controller){
		Frame frame = controller.frame();
		
		// FRAME
		System.out.println("Frame id:"+ frame.id() + ",Timestamp:"+ frame.timestamp()
							+",No of Hands:"+ frame.hands().count()
							+ ",No of fingers:"+ frame.fingers().count()
							+ ",No of Tools: "+ frame.tools().count()
							+ "No of gestures:"+ frame.gestures().count()); 
		
		// HAND
		for(Hand hand : frame.hands()){
			String handType = hand.isLeft() ? "Left Hand" : "Right Hand";
			System.out.println(handType + "  " + ", Id: "+ hand.id() 
								+ ", Palm Position: " + hand.palmPosition());
			
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();
			
			System.out.println("Pitch: "+ Math.toDegrees(direction.pitch())
								+ " Roll: "+ Math.toDegrees(normal.roll())
								+ " Yaw: "+ Math.toDegrees(direction.yaw())); 
		}
		
			
		// FINGER
		for(Finger finger : frame.fingers()) {
			System.out.println("Finger Type: "+ finger.type()
								+ " Finger Id:" + finger.id()
								+ " Finger length(mm):" + finger.length()
								+ " Finger Width:" + finger.width());
			
			for(Bone.Type boneType : Bone.Type.values()) {
				Bone bone = finger.bone(boneType);
				System.out.println("Bone Type: " + bone.type()
									+ " Start:" + bone.prevJoint()
									+ " End: " + bone.nextJoint()
									+ " Direction: "+ bone.direction());
			}
		} 
		
		// Tool 
		for(Tool tool : frame.tools()) {
			System.out.println(" Tool id: "+ tool.id()
								+ " Tip Position: " + tool.tipPosition()
								+ " Direction: "+ tool.direction()
								+ " Width: "+ tool.width()
								+ " Touch Distance:"+ tool.touchDistance());
		} 
		
		GestureList gestures = frame.gestures();
		for(int i = 0; i< gestures.count(); i++){
			Gesture gesture = gestures.get(i);
			
			switch (gesture.type()) {
				case TYPE_CIRCLE:
					CircleGesture circle = new CircleGesture(gesture);
					String clockwiseness;
					if(circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4){
						clockwiseness = "clockwise";
						
					}
					else{
						clockwiseness = "counterclockwise";
					}
					
					double sweptAngle = 0;
					if(circle.state()!= State.STATE_START) {
							CircleGesture previous = new CircleGesture(controller.frame(1).gesture(circle.id()));
							sweptAngle = (circle.progress() - previous.progress()) * 2 * Math.PI;
					}
					
					System.out.println("Circle id:" + circle.id()
									+ " State: " + circle.state()
									+ " Progress: " + circle.progress()
									+ " Radius: "+ circle.radius()
									+ " Angle:" + Math.toDegrees(sweptAngle)
									+ " "+ clockwiseness);
					break;
				
				case TYPE_SWIPE:
					SwipeGesture swipe = new SwipeGesture(gesture);
					System.out.println(" Swipe ID: "+ swipe.id()
										+ " State: "+ swipe.state()
										+" Swipe Position: "+ swipe.position()
										+ " Direction: "+ swipe.direction()
										+ " Speed: "+ swipe.speed());
					break;
				case TYPE_SCREEN_TAP:
					ScreenTapGesture screentap = new ScreenTapGesture(gesture);
					System.out.println(" Tap id:"+ screentap.id()
										+ " State: " + screentap.state()
										+ " Position: "+ screentap.position()
										+ " Direction:" + screentap.direction());
					break;
				case TYPE_KEY_TAP:
					KeyTapGesture keygesture = new KeyTapGesture(gesture);
					System.out.println("Id: "+ keygesture.id()
										+ " State:" + keygesture.state()
										+ " Position:"+ keygesture.position()
										+ " Direction: "+ keygesture.direction());
					
					break;
				default: 
					System.out.println("Unknown gesture");
					break;
			}
		}
		
		
	}
}

public class LeapController {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LeapListner listener = new LeapListner();
		Controller controller = new Controller();
		
		controller.addListener(listener);
		System.out.println("Press enter to quit");
		
		try{
			System.in.read();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		controller.removeListener(listener);

	}

}
