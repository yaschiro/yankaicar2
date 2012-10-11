package com.example.yankaicar2;


public class VideoSmackCarActivity {}
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.jivesoftware.smack.Chat;
//import org.jivesoftware.smack.ChatManager;
//import org.jivesoftware.smack.ChatManagerListener;
//import org.jivesoftware.smack.Connection;
//import org.jivesoftware.smack.MessageListener;
//import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.RosterEntry;
//import org.jivesoftware.smack.RosterGroup;
//import org.jivesoftware.smack.RosterListener;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.packet.DefaultPacketExtension;
//import org.jivesoftware.smack.packet.Message;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.util.Base64;
//import org.jivesoftware.smack.util.StringUtils;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Rect;
//import android.hardware.Camera;
//import android.hardware.Camera.PictureCallback;
//import android.hardware.Camera.PreviewCallback;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Toast;
//
//public class VideoSmackCarActivity extends Activity implements
//		SurfaceHolder.Callback, PreviewCallback, Runnable {
//	 
//	private static final String TAG = "VideoSmackCar";
//	private static final boolean D = true; // Debugģʽ������ʶ
//	public static final String BroadcastName = "android.lynx.car.intent.INTENT_CAR_CONTROL"; // ���������ƹ㲥��ַ
//	private Chat newChat = null;
//	private Handler mHandler = new Handler();
//	public Connection connection;
//	private ChatManager chatmanager;
//	private SettingsDialog mDialog;
//	private EditText mRecipient;
//	private EditText mSendText;
//	private ListView mList;
//	public String serverName = "gamil.com";
//	private ArrayList<String> messages = new ArrayList<String>();
//
//	// ����ʹ��
//	private Button btn_capture;
//	private Button btn_shotcut;
//	private Camera camera = null;
//	private SurfaceHolder mSurfaceHolder;
//	private SurfaceView mSurfaceView01;
//	private boolean bPreviewing = false;
//	private int intScreenWidth;
//	private int intScreenHeight;
//
//	//����ͼ��֡��
//	private byte[] frameData;
//	
//	//�ػ�������
//	private Thread Guarder = null;
//	private boolean ThreadFlag = false;
//	private int cycleMiliseconds = 2000;  //����ѭ�����ڣ�Ĭ������Ϊ2000���루2�룩
//
//	// ��չ���ݶ���
//	public static final String EElementName = "JpegExtension";
//	public static final String ENameSpace = "CarExtension";
//	public static final String EValueName = "AJpeg";
//	public static final String ETimeName = "CreateTime";
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.main);
//
//		/* ȡ����Ļ�������� */
//		getDisplayMetrics();
//		findViews();
//		getSurfaceHolder();
//
//		btn_capture.setOnClickListener(new Button.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				jumpOutAToast("����");
//				takeAPicture();
//			}
//		});
//
//		btn_shotcut.setOnClickListener(new Button.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				jumpOutAToast("��ͼ");
//				takeAShotCut();
//			}
//		});
//
//		mRecipient = (EditText) this.findViewById(R.id.recipient);
//		mSendText = (EditText) this.findViewById(R.id.sendText);
//		mList = (ListView) this.findViewById(R.id.listMessages);
//		// Dialog for getting the xmpp settings
//		mDialog = new SettingsDialog(this);
//
//		// Set a listener to show the settings dialog
//		Button setup = (Button) this.findViewById(R.id.setup);
//		setup.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				mHandler.post(new Runnable() {
//					@Override
//					public void run() {
//						mDialog.show();
//					}
//				});
//			}
//		});
//
//		Button send = (Button) this.findViewById(R.id.send);
//		send.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				String to = mRecipient.getText().toString();
//				String text = mSendText.getText().toString();
//				Message msg = new Message(to, Message.Type.chat);
//				msg.setBody(text);
//
//				sendMessage(msg);
//
//			}
//		});
//		setListAdapter();
//	}
//
//	/**
//	 * ͳһ����Ϣ���ͷ���
//	 * 
//	 * @param msg
//	 *            Ҫ���͵���Ϣ
//	 */
//	private void sendMessage(Message msg) {
//		while (messages.size() > 50) // ��ֹ��Ϣ����
//		{
//			messages.remove(messages.size() - 1);
//		}
//		messages.add(0, msg.getBody());
//		messages.add(0, connection.getUser() + ":");
//
//		// �½�һ���Ự
//		if (newChat == null) {// "test@a-7a511a1a957b4"
//			String fName = mRecipient.getText().toString();
//			if (!fName.contains("@"))
//				fName = fName + "@" + serverName;
//			newChat = chatmanager.createChat(fName, new MessageListener() {
//				public void processMessage(Chat chat, Message message) {
//					System.out.println("Received from ��" + message.getFrom()
//							+ "�� message: " + message.getBody());
//				}
//			});
//		}
//		try {
//			newChat.sendMessage(msg);
//		} catch (XMPPException e) {
//			e.printStackTrace();
//		}
//
//		// Add the incoming message to the list view
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				setListAdapter();
//			}
//		});
//	}
//
//	/**
//	 * Called by Settings dialog when a connection is establised with the XMPP
//	 * server
//	 * 
//	 * @param connection
//	 */
//	public void setConnection(Connection connection) {
//		this.connection = (Connection) connection;
//		if (connection != null) {
//			try {
//				// XMPPConnection.DEBUG_ENABLED = true;
//				// //�ҵĵ���IP:10.16.25.90
//				// final ConnectionConfiguration connectionConfig = new
//				// ConnectionConfiguration("192.168.8.194", 5222,
//				// "a-7a511a1a957b4");
//				// connectionConfig.setSASLAuthenticationEnabled(false);
//				// connection = new XMPPConnection(connectionConfig);
//				// connection.connect();//����
//				// connection.login("whb", "874553");//��½
//				chatmanager = connection.getChatManager();
//
//				// ��������������Ϣ����㲥��Ϣ������
//				chatmanager.addChatListener(new ChatManagerListener() {
//					@Override
//					public void chatCreated(Chat chat, boolean createdLocally) {
//						chat.addMessageListener(new MessageListener() {
//							@Override
//							public void processMessage(Chat chat,
//									Message message) {
//								if (message.getBody() != null) {
//
//									MessageHook(message); // ���ڼ������������Ϣ����
//
//									String fromName = StringUtils
//											.parseBareAddress(message.getFrom());
//									while (messages.size() > 50) // ��ֹ��Ϣ����
//									{
//										messages.remove(messages.size() - 1);
//									}
//									messages.add(0, message.getBody());
//									messages.add(0, fromName + ":");
//									// Add the incoming message to the list view
//									mHandler.post(new Runnable() {
//										@Override
//										public void run() {
//											setListAdapter();
//										}
//									});
//								}
//							}
//
//						});
//					}
//				});
//				// ������Ϣ
//				// newChat.sendMessage("���ǲ���");
//
//				// ��ȡ������
//				Roster roster = connection.getRoster();
//				Collection<RosterEntry> entries = roster.getEntries();
//				for (RosterEntry entry : entries) {
//					System.out.print(entry.getName() + " - " + entry.getUser()
//							+ " - " + entry.getType() + " - "
//							+ entry.getGroups().size());
//					Presence presence = roster.getPresence(entry.getUser());
//					System.out.println(" - " + presence.getStatus() + " - "
//							+ presence.getFrom());
//				}
//
//				// ��ӻ��������������������״̬�ĸı䡣
//				roster.addRosterListener(new RosterListener() {
//
//					@Override
//					public void entriesAdded(Collection<String> addresses) {
//						System.out.println("entriesAdded");
//					}
//
//					@Override
//					public void entriesUpdated(Collection<String> addresses) {
//						System.out.println("entriesUpdated");
//					}
//
//					@Override
//					public void entriesDeleted(Collection<String> addresses) {
//						System.out.println("entriesDeleted");
//					}
//
//					@Override
//					public void presenceChanged(Presence presence) {
//						System.out.println("presenceChanged - >"
//								+ presence.getStatus());
//					}
//
//				});
//
//				// ������
//				// /RosterGroup group = roster.createGroup("��ѧ");
//				// for(RosterEntry entry : entries) {
//				// group.addEntry(entry);
//				// }
//				for (RosterGroup g : roster.getGroups()) {
//					for (RosterEntry entry : g.getEntries()) {
//						System.out.println("Group " + g.getName() + " >> "
//								+ entry.getName() + " - " + entry.getUser()
//								+ " - " + entry.getType() + " - "
//								+ entry.getGroups().size());
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void setListAdapter() {
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				R.layout.multi_line_list_item, messages);
//		mList.setAdapter(adapter);
//	}
//
//	@Override
//	protected void onDestroy() {
//		ThreadDestory();  //�������ػ�����
//		connection.disconnect();
//		System.exit(0);
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onPause() {
//		// ����������̨ʱִ��
//		// camera.release();
//		// camera = null;
//		bPreviewing = false;
//		super.onPause();
//	}
//
//	@Override
//	public void onStop() {
//		resetCamera();
//		super.onStop();
//	}
//
//	@Override
//	protected void onResume() {
//
//		try {
//			initCamera();
//		} catch (IOException e) {
//			Log.e(TAG, "initCamera() in Resume() erorr!");
//		}
//		super.onResume();
//	}
//
//	@Override
//	public void onPreviewFrame(byte[] data, Camera camera) {
//
//		frameData = data.clone();
//
//		// get the prew frame here,the data of default is YUV420_SP
//
//		// you should change YUV420_SP to YUV420_P
//
//	}
//
//	/*
//	 * function: ��previewʱ��ʵ����Camera,��ʼpreview ��previewʱand�����ʱ��������һ��preview
//	 * previewʱ��������
//	 */
//	private void initCamera() throws IOException {
//		if (!bPreviewing) {
//			/* ���������Ԥ��ģʽ�������� */
//			camera = Camera.open();
//		}
//		// ��Ԥ��ʱand�����ʱ������preview
//		if (camera != null && !bPreviewing) {
//			Log.i(TAG, "inside the camera");
//			/* ����Camera.Parameters���� */
//			Camera.Parameters parameters = camera.getParameters();
//			/* ������Ƭ��ʽΪJPEG */
//			// parameters.setPictureFormat(PixelFormat.JPEG);
//			/* ָ��preview����Ļ��С */
//			// parameters.setPreviewSize(intScreenWidth, intScreenHeight);
//			/* ����ͼƬ�ֱ��ʴ�С (������) */
//			// parameters.setPictureSize(intScreenWidth, intScreenHeight);
//			/* ��Camera.Parameters������Camera */
//			camera.setParameters(parameters);
//			/* setPreviewDisplayΨһ�Ĳ���ΪSurfaceHolder */
//			camera.setPreviewDisplay(mSurfaceHolder);
//			camera.setPreviewCallback(this);// ����Ԥ��֡�Ľӿ�,����ͨ������ӿڣ����������Ԥ��֡�����ݵ�
//			/* ��������Preview */
//			camera.startPreview();
//			bPreviewing = true;
//		}
//	}
//
//	/* func:ֹͣpreview,�ͷ�Camera���� */
//	private void resetCamera() {
//		if (camera != null && bPreviewing) {
//			camera.stopPreview();
//			/* �ͷ�Camera���� */
//			camera.release();
//			camera = null;
//			bPreviewing = false;
//		}
//	}
//
//	private void takeAPicture() {
//		if (camera != null && bPreviewing) {
//			/* ����takePicture()�������� */
//			camera.takePicture(null, null, jpegCallback);// ����PictureCallback
//															// interface�Ķ�����Ϊ����
//		}
//	}
//
//	private void takeAShotCut() {
//		if (camera != null && bPreviewing) {
//			/* ���ý�ͼ�������շ������� */
//			// Bitmap newBitmap = mSurfaceView01.getDrawingCache();
//			/*
//			 * Bitmap newBitmap = this.makebitmap(this); newBitmap =
//			 * BitmapFactory.decodeByteArray(YUVData, 0, YUVData.length);
//			 */
//			byte[] YUVData = frameData.clone();
//			int imageWidth = camera.getParameters().getPreviewSize().width;
//			int imageHeight = camera.getParameters().getPreviewSize().height;
//			int RGBData[] = new int[imageWidth * imageHeight];
//			byte[] mYUVData = new byte[YUVData.length];
//			System.arraycopy(YUVData, 0, mYUVData, 0, YUVData.length);
//			decodeYUV420SP(RGBData, mYUVData, imageWidth, imageHeight);
//
//			Bitmap newBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
//					Bitmap.Config.ARGB_8888);
//			newBitmap.setPixels(RGBData, 0, imageWidth, 0, 0, imageWidth,
//					imageHeight);
//
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//			byte[] jpegArray = stream.toByteArray();
//
//			/*
//			 * ����ͼƬ����ش���
//			 */
//			String to = mRecipient.getText().toString();
//			String text = "ShotCut-JPEG";
//			Message msg = new Message(to, Message.Type.chat);
//			msg.setBody(text);
//
//			DefaultPacketExtension jpegExtension = new DefaultPacketExtension(
//					EElementName, ENameSpace);
//			jpegExtension.setValue(EValueName, Base64.encodeBytes(jpegArray));
//			jpegExtension.setValue(ETimeName, System.currentTimeMillis()+"");
//			msg.addExtension(jpegExtension);
//			
//
//			if (D)
//				Log.d(TAG,
//						"+++ Jpeg Extended +++"
//								+ Base64.encodeBytes(jpegArray)
//										.substring(0, 40));
//
//			sendMessage(msg);
//		}
//	}
//
//	/**
//	 * ת��YUV420SP��rgb�Ĵ���
//	 * 
//	 * @param rgb
//	 * @param yuv420sp
//	 * @param width
//	 * @param height
//	 */
//	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
//			int height) {
//		final int frameSize = width * height;
//		for (int j = 0, yp = 0; j < height; j++) {
//			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
//			for (int i = 0; i < width; i++, yp++) {
//				int y = (0xff & ((int) yuv420sp[yp])) - 16;
//				if (y < 0)
//					y = 0;
//				if ((i & 1) == 0) {
//					v = (0xff & yuv420sp[uvp++]) - 128;
//					u = (0xff & yuv420sp[uvp++]) - 128;
//				}
//				int y1192 = 1192 * y;
//				int r = (y1192 + 1634 * v);
//				int g = (y1192 - 833 * v - 400 * u);
//				int b = (y1192 + 2066 * u);
//				if (r < 0)
//					r = 0;
//				else if (r > 262143)
//					r = 262143;
//				if (g < 0)
//					g = 0;
//				else if (g > 262143)
//					g = 262143;
//				if (b < 0)
//					b = 0;
//				else if (b > 262143)
//					b = 262143;
//				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
//						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//			}
//		}
//	}
//
//	private Bitmap makebitmap(Activity activity) {
//		// View������Ҫ��ͼ��View
//		View view = activity.getWindow().getDecorView();
//		view.setDrawingCacheEnabled(true);
//		view.buildDrawingCache();
//		Bitmap b1 = view.getDrawingCache();
//
//		Rect frame = new Rect();
//		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		int statusBarHeight = frame.top;
//
//		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//		int height = activity.getWindowManager().getDefaultDisplay()
//				.getHeight();
//		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
//				- statusBarHeight);
//		view.destroyDrawingCache();
//		return b;
//	}
//
//	/* func:��ȡ��Ļ�ֱ��� */
//	private void getDisplayMetrics() {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		intScreenWidth = dm.widthPixels;
//		intScreenHeight = dm.heightPixels;
//		Log.i(TAG, Integer.toString(intScreenWidth));
//	}
//
//	private PictureCallback jpegCallback = new PictureCallback() {
//		public void onPictureTaken(byte[] data, Camera camera) {
//			/*
//			 * resetCamera(); try { initCamera(); } catch(Exception e) {
//			 * Log.e(TAG, "initCamera Error after snapping"); }
//			 */
//			camera.startPreview();
//
//			/*
//			 * ����ͼƬ����ش���
//			 */
//			String to = mRecipient.getText().toString();
//			String text = "Photo-JPEG";
//			Message msg = new Message(to, Message.Type.chat);
//			msg.setBody(text);
//
//			DefaultPacketExtension jpegExtension = new DefaultPacketExtension(
//					EElementName, ENameSpace);
//			jpegExtension.setValue(EValueName, Base64.encodeBytes(data));
//			jpegExtension.setValue(ETimeName, System.currentTimeMillis()+"");
//			msg.addExtension(jpegExtension);
//
//			if (D)
//				Log.d(TAG, "+++ Jpeg Extended +++"
//						+ Base64.encodeBytes(data).substring(0, 40));
//
//			sendMessage(msg);
//
//		}
//	};
//
//	/* get a fully initialized SurfaceHolder */
//	private void getSurfaceHolder() {
//
//		// ʹSurfaceView���Ա���ͼ
//		mSurfaceView01.setDrawingCacheEnabled(true);
//
//		mSurfaceHolder = mSurfaceView01.getHolder();
//		mSurfaceHolder.addCallback(this);
//		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//	}
//
//	/* func:����toast,��Ҫ�������� */
//	private void jumpOutAToast(String string) {
//		Toast.makeText(VideoSmackCarActivity.this, string, Toast.LENGTH_SHORT)
//				.show();
//	}
//
//	/* func:�������Ԫ��Button capture,FrameLayout frame */
//	private void findViews() {
//		btn_capture = (Button) findViewById(R.id.btn_capture);
//		btn_shotcut = (Button) findViewById(R.id.btn_shotcut);
//		/* ��SurfaceView��Ϊ���Preview֮�� */
//		mSurfaceView01 = (SurfaceView) findViewById(R.id.surfaceView1);
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		if (!bPreviewing) {
//			camera = Camera.open();
//		}
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//		if (bPreviewing) {
//			camera.stopPreview();
//		}
//		Camera.Parameters params = camera.getParameters();
//		// params.setPreviewSize(width, height);
//		camera.setParameters(params);
//		try {
//			camera.setPreviewDisplay(mSurfaceHolder);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		camera.startPreview();
//		bPreviewing = true;
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		camera.stopPreview();
//		bPreviewing = false;
//		camera.release();
//		mSurfaceHolder = null;
//	}
//
//	/**
//	 * ���ڼ����Ϣ�Ĺ��Ӻ���������������Ⲣִ�п������
//	 * 
//	 * @param messageIn
//	 *            ��Ҫ��������Ϣ
//	 */
//	private void MessageHook(Message messageIn) {
//		boolean needReply = false;
//		boolean isFound = false;
//		String HookResult = "";
//
//		if (D)
//			Log.d(TAG, "+++ ON HOOK +++");
//
//		// ����������ʽ���жϲ���ȡ��������
//		String input = messageIn.getBody();
//
//		// #command action turnL 64 moveF 7 keep 200
//		Pattern pattern = Pattern
//				.compile("^#command\\saction\\sturn([LR])\\s(\\d+)\\smove([FB])\\s(\\d+)\\skeep\\s(\\d+)");
//		Matcher matcher = pattern.matcher(input);
//		String LR = "";
//		String LRNum = "";
//		String FB = "";
//		String FBNum = "";
//		String Keep = "";
//		int LRInt = 0;
//		int FBInt = 0;
//		int KeepInt = 0;
//		double LRDouble = 0;
//		double FBDouble = 0;
//		while (matcher.find()) { // Find each match in turn
//			// Access a submatch group
//			LR = matcher.group(1);
//			LRNum = matcher.group(2);
//			FB = matcher.group(3);
//			FBNum = matcher.group(4);
//			Keep = matcher.group(5);
//
//			needReply = true;
//			isFound = true;
//			if (D)
//				Log.d(TAG, "+++ Found Action +++");
//		}
//		if (isFound) {
//			try // ����ת��INT��֮����û����������ʽ���ж�Խ����ӦΪ�����Ĵ���������Ķ���
//			{
//				LRInt = Integer.parseInt(LRNum);
//				FBInt = Integer.parseInt(FBNum);
//				KeepInt = Integer.parseInt(Keep);
//			} catch (Exception e) {
//				HookResult = "�������ֵ����ת��Ϊ����";
//				if (D)
//					Log.d(TAG, "+++ Not Integer +++");
//				e.printStackTrace();
//			}
//			// �ж�int��ֵ�Ƿ�Խ��
//			if (LRInt < 0 || LRInt > 100 || FBInt < 0 || FBInt > 100
//					|| KeepInt < 0 || KeepInt > 1000) {
//				if (D)
//					Log.d(TAG, "+++ Out of bond +++");
//				HookResult = "�������ֵԽ�磬��ע��ת����ٶ�ֵΪ0~100������������ʱ��Ϊ0~1000������������";
//			} else {
//				if (LR.equals("L")) {
//					if (FB.equals("F")) {
//						if (D)
//							Log.d(TAG, "+++ LF +++");
//						LRDouble = LRInt * (-0.01);
//						FBDouble = FBInt * (0.01);
//						BroadContralCommand(LRDouble, FBDouble, KeepInt);
//						HookResult = "�յ������ת" + LRInt + "%���ٶ�" + FBInt
//								+ "%��ǰ��" + KeepInt + "����";
//					} else {
//						if (D)
//							Log.d(TAG, "+++ LB +++");
//						LRDouble = LRInt * (-0.01);
//						FBDouble = FBInt * (-0.01);
//						BroadContralCommand(LRDouble, FBDouble, KeepInt);
//						HookResult = "�յ������ת" + LRInt + "%���ٶ�" + FBInt
//								+ "%������" + KeepInt + "����";
//					}
//				} else {
//					if (FB.equals("F")) {
//						if (D)
//							Log.d(TAG, "+++ RF +++");
//						LRDouble = LRInt * (0.01);
//						FBDouble = FBInt * (0.01);
//						BroadContralCommand(LRDouble, FBDouble, KeepInt);
//						HookResult = "�յ������ת" + LRInt + "%���ٶ�" + FBInt
//								+ "%��ǰ��" + KeepInt + "����";
//					} else {
//						if (D)
//							Log.d(TAG, "+++ RB +++");
//						LRDouble = LRInt * (0.01);
//						FBDouble = FBInt * (-0.01);
//						BroadContralCommand(LRDouble, FBDouble, KeepInt);
//						HookResult = "�յ������ת" + LRInt + "%���ٶ�" + FBInt
//								+ "%������" + KeepInt + "����";
//					}
//				}
//
//			}
//		}
//		isFound = false;  //�������Ϊ�˷�ֹ�´εĽ�����������׸���ǰ��Ľ����ɾ�����Ծ�֪����
//
//		// #command eye on every 2000
//		pattern = Pattern
//				.compile("^#command\\seye\\son\\severy\\s(\\d+)");
//		matcher = pattern.matcher(input);
//		String Cycle = "";
//		int CycleInt = 0;
//		while (matcher.find()) { // Find each match in turn
//			// Access a submatch group
//			Cycle = matcher.group(1);
//			
//			needReply = true;
//			isFound = true;
//			if (D)
//				Log.d(TAG, "+++ Found Eye On +++");
//		}
//		if (isFound) {
//			try // ����ת��INT��֮����û����������ʽ���ж�Խ����ӦΪ�����Ĵ���������Ķ���
//			{
//				CycleInt = Integer.parseInt(Cycle);
//			} catch (Exception e) {
//				HookResult = "�������ֵ����ת��Ϊ����";
//				if (D)
//					Log.d(TAG, "+++ Not Integer +++");
//				e.printStackTrace();
//			}
//			// �ж�int��ֵ�Ƿ�Խ��
//			if (CycleInt < 0 || CycleInt > 10000) {
//				if (D)
//					Log.d(TAG, "+++ Out of bond +++");
//				HookResult = "�������ֵԽ�磬��ע���������ֵΪ0~10000��0~10�룩��������";
//			} else {
//				//Զ���۴�
//				cycleMiliseconds = CycleInt;
//				ThreadCreate();
//				HookResult = "Զ�����Ѵ�";
//			}
//		}
//		isFound = false;  //�������Ϊ�˷�ֹ�´εĽ�����������׸���ǰ��Ľ����ɾ�����Ծ�֪����
//
//		// #command eye off
//		pattern = Pattern.compile("^#command\\seye\\soff");
//		matcher = pattern.matcher(input);
//		while (matcher.find()) { // Find each match in turn
//			// Access a submatch group
//			needReply = true;
//			isFound = true;
//			if (D)
//				Log.d(TAG, "+++ Found Eye Off +++");
//		}
//		if (isFound) {
//			//Զ���۹ر�
//			ThreadDestory();
//			HookResult = "Զ�����ѹر�";
//		}
//		isFound = false;  //�������Ϊ�˷�ֹ�´εĽ�����������׸���ǰ��Ľ����ɾ�����Ծ�֪����
//
//		// #command eye photo
//		pattern = Pattern.compile("^#command\\seye\\sphoto");
//		matcher = pattern.matcher(input);
//		while (matcher.find()) { // Find each match in turn
//			// Access a submatch group
//			needReply = true;
//			isFound = true;
//			if (D)
//				Log.d(TAG, "+++ Found Eye Photo +++");
//		}
//		if (isFound) {
//			//ִ�����ද��
//			takeAPicture();
//			HookResult = "Զ����������...";
//		}
//		isFound = false;  //�������Ϊ�˷�ֹ�´εĽ�����������׸���ǰ��Ľ����ɾ�����Ծ�֪����
//
//		if (needReply) // �����Ҫ�ͷ��ͻظ�
//		{
//			if (D)
//				Log.d(TAG, "+++ To Find Chat +++");
//			if (newChat != null) // ֻ�н���chat�Ժ�Ž��лظ�
//			{
//				if (D)
//					Log.d(TAG, "+++ Reply message +++");
//				while (messages.size() > 50) // ��ֹ��Ϣ����
//				{
//					messages.remove(messages.size() - 1);
//				}
//				messages.add(0, HookResult);
//				messages.add(0, "MessageHook" + ":");
//
//				String to = mRecipient.getText().toString();
//				Message msg = new Message(to, Message.Type.chat);
//				msg.setBody(HookResult);
//
//				try {
//					newChat.sendMessage(msg);
//				} catch (XMPPException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * ������С�����Ƴ����Ϳ��ƹ㲥
//	 * 
//	 * @param turn
//	 *            ת��Ĳ�����0Ϊ���У�-1Ϊ��ת���Ƕȣ�1Ϊ��ת���Ƕȡ�
//	 * @param move
//	 *            �ƶ��Ĳ�����0Ϊ��ֹ��-1Ϊ��������ٶȣ�1Ϊǰ������ٶȡ�
//	 * @param keep
//	 *            �ö���������ʱ�䣨0~1000���룩
//	 */
//	private void BroadContralCommand(double turn, double move, int keep) {
//		Intent intent = new Intent(BroadcastName);
//		intent.putExtra("Type", "Action");
//		intent.putExtra("MilliSeconds", keep);
//		intent.putExtra("TurnPosition", turn);
//		intent.putExtra("MovePosition", move);
//		this.sendBroadcast(intent);
//	}
//	
//	/**
//	 * ��ʱ����λ��״̬���ػ����̶���
//	 */
//	@Override
//	public void run() {
//		while (ThreadFlag) {
//			
//			//ÿһ������ִ��һ�ν�ͼ����������ģ���֡����Ƶ��
//			takeAShotCut();
//			
//			try {
//				Thread.sleep(cycleMiliseconds);
//			} catch (Exception ex) {
//				if (D)
//					Log.d(TAG, "˯���������⡭�������ܰɣ�����");
//			}
//		}
//	}
//	
//	/**
//	 * �������ڶ�ʱ����λ��״̬���ػ�����
//	 */
//	public void ThreadCreate()
//	{
//		if(Guarder != null)  //ȷ��ֻ��һ���ػ����̿��Ա�����
//			return;
//		Guarder = new Thread(this);
//		ThreadFlag = true;
//		Guarder.start();
//	}
//	
//	/**
//	 * �����ػ�����
//	 */
//	public void ThreadDestory()
//	{
//		ThreadFlag = false;
//		Guarder = null;		
//	}
//	
//} 