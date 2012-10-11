package com.example.yankaicar2;


 

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
 
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
 
public class VideoSmackCarControllerActivity extends Activity {
	 
	private static final String TAG = "VideoSmackCarController";
	private static final boolean D = true; // Debugģʽ������ʶ
	private Chat newChat = null;
	private Handler mHandler = new Handler();
	public XMPPConnection connection;
	private ChatManager chatmanager;
	private SettingsDialog mDialog;
	private EditText mRecipient;
	private EditText mSendText;
	private EditText mCycleTime;
	private RadioGroup rg;
	private RadioButton b0;
	private RadioButton b1;
	private RadioButton b2;
	private RadioButton b3;
	private ListView mList;
	private ImageView mImageView;
	public String serverName = "gamil.com";
	private ArrayList<String> messages = new ArrayList<String>();

	// ��չ���ݶ���
	public static final String EElementName = "JpegExtension";
	public static final String ENameSpace = "CarExtension";
	public static final String EValueName = "AJpeg";
	public static final String ETimeName = "CreateTime";

	// ����ȷ��ͼƬ���Ⱥ�ʱ���ֹͼƬ���ݰ���ʱ���������ʾ����
	private long lastPhotoTime = 0;

	// ���ڴ洢�������������ʱ�����
	private int moveContinueTime = 100;
	private int eyeCycleTime = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); // ȥ��������
		setContentView(R.layout.main);

		initFindsAndListeners();

	}

	/**
	 * ͳһ�Ĳ��Ҵ���Ͱ�ť�����¼�������������Ϊ��������ת����ʱ�������
	 */
	private void initFindsAndListeners() {
		mRecipient = (EditText) this.findViewById(R.id.recipient);
		mSendText = (EditText) this.findViewById(R.id.sendText);
		mList = (ListView) this.findViewById(R.id.listMessages);
		mImageView = (ImageView) this.findViewById(R.id.imageView1);
		// Dialog for getting the xmpp settings
		mDialog = new SettingsDialog(this);

		// Set a listener to show the settings dialog
		Button setup = (Button) this.findViewById(R.id.setup);
		setup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mDialog.show();
					}
				});
			}
		});

		Button send = (Button) this.findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = mSendText.getText().toString();
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);

				while (messages.size() > 50) // ��ֹ��Ϣ����
				{
					messages.remove(messages.size() - 1);
				}
				messages.add(0, text);
				messages.add(0, connection.getUser() + ":");

				// �½�һ���Ự
				if (newChat == null) {// "test@a-7a511a1a957b4"
					String fName = mRecipient.getText().toString();
					if (!fName.contains("@"))
						fName = fName + "@" + serverName;
					newChat = chatmanager.createChat(fName,
							new MessageListener() {
								public void processMessage(Chat chat,
										Message message) {
									System.out.println("Received from ��"
											+ message.getFrom() + "�� message: "
											+ message.getBody());
								}
							});
				}
				try {
					newChat.sendMessage(msg);
				} catch (XMPPException e) {
					e.printStackTrace();
				}

				// Add the incoming message to the list view
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						setListAdapter();
					}
				});
			}
		});
		setListAdapter();

		/*
		 * ���￪ʼ�Ǻ������ְ�ť�ĳ�ʼ��
		 */
		Button MoveLF = (Button) this.findViewById(R.id.buttonLF);
		MoveLF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 40 moveF 50 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveF = (Button) this.findViewById(R.id.buttonF);
		MoveF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 0 moveF 50 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveRF = (Button) this.findViewById(R.id.buttonRF);
		MoveRF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnR 40 moveF 50 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveL = (Button) this.findViewById(R.id.buttonL);
		MoveL.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 100 moveF 40 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveM = (Button) this.findViewById(R.id.buttonM);
		MoveM.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 0 moveF 0 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveR = (Button) this.findViewById(R.id.buttonR);
		MoveR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnR 100 moveF 40 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveLB = (Button) this.findViewById(R.id.buttonLB);
		MoveLB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 100 moveB 40 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveB = (Button) this.findViewById(R.id.buttonB);
		MoveB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnL 0 moveB 50 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button MoveRB = (Button) this.findViewById(R.id.buttonRB);
		MoveRB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command action turnR 100 moveB 40 keep "
						+ moveContinueTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});

		// Eye��ذ�ť�������ʼ��
		mCycleTime = (EditText) this.findViewById(R.id.editTextCycle);

		Button EyeOn = (Button) this.findViewById(R.id.buttonEyeOn);
		EyeOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int NumGet = 0;
				try {
					NumGet = Integer.parseInt(mCycleTime.getText().toString());
					eyeCycleTime = NumGet;
				} catch (NumberFormatException e) {
					if (D)
						Log.d(TAG, "+++ Input Must Be An Integer +++");
				}

				String to = mRecipient.getText().toString();
				String text = "#command eye on every " + eyeCycleTime;
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button EyeOff = (Button) this.findViewById(R.id.buttonEyeOff);
		EyeOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command eye off";
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});
		Button EyePhoto = (Button) this.findViewById(R.id.buttonPhoto);
		EyePhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String to = mRecipient.getText().toString();
				String text = "#command eye photo";
				Message msg = new Message(to, Message.Type.chat);
				msg.setBody(text);
				sendMessage(msg);
			}
		});

		// ��ѡ��ť���÷���
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		b0 = (RadioButton) findViewById(R.id.radio0);
		b1 = (RadioButton) findViewById(R.id.radio1);
		b2 = (RadioButton) findViewById(R.id.radio2);
		b3 = (RadioButton) findViewById(R.id.radio3);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == b0.getId()) {
					moveContinueTime = 800;
				} else if (checkedId == b1.getId()) {
					moveContinueTime = 200;
				} else if (checkedId == b2.getId()) {
					moveContinueTime = 100;
				} else if (checkedId == b3.getId()) {
					moveContinueTime = 50;
				}
			}

		});
	}

	/**
	 * ͳһ����Ϣ���ͷ���
	 * 
	 * @param msg
	 *            Ҫ���͵���Ϣ
	 */
	private void sendMessage(Message msg) {
		while (messages.size() > 50) // ��ֹ��Ϣ����
		{
			messages.remove(messages.size() - 1);
		}
		messages.add(0, msg.getBody());
		messages.add(0, connection.getUser() + ":");

		// �½�һ���Ự
		if (newChat == null) {// "test@a-7a511a1a957b4"
			String fName = mRecipient.getText().toString();
			if (!fName.contains("@"))
				fName = fName + "@" + serverName;
			newChat = chatmanager.createChat(fName, new MessageListener() {
				public void processMessage(Chat chat, Message message) {
					System.out.println("Received from ��" + message.getFrom()
							+ "�� message: " + message.getBody());
				}
			});
		}
		try {
			newChat.sendMessage(msg);
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		// Add the incoming message to the list view
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				setListAdapter();
			}
		});
	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = (XMPPConnection) connection;
		if (connection != null) {
			try {
				// XMPPConnection.DEBUG_ENABLED = true;
				// //�ҵĵ���IP:10.16.25.90
				// final ConnectionConfiguration connectionConfig = new
				// ConnectionConfiguration("192.168.8.194", 5222,
				// "a-7a511a1a957b4");
				// connectionConfig.setSASLAuthenticationEnabled(false);
				// connection = new XMPPConnection(connectionConfig);
				// connection.connect();//����
				// connection.login("whb", "874553");//��½
				chatmanager = connection.getChatManager();

				// ��������������Ϣ����㲥��Ϣ������
				chatmanager.addChatListener(new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean createdLocally) {
						chat.addMessageListener(new MessageListener() {
							@Override
							public void processMessage(Chat chat,
									Message message) {
								if (message.getBody() != null) {

									MessageHook(message); // ���ڼ������������Ϣ����

									String fromName = StringUtils
											.parseBareAddress(message.getFrom());
									while (messages.size() > 50) // ��ֹ��Ϣ����
									{
										messages.remove(messages.size() - 1);
									}
									messages.add(0, message.getBody());
									messages.add(0, fromName + ":");
									// Add the incoming message to the list view
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											setListAdapter();
										}
									});
								}
							}

						});
					}
				});
				// ������Ϣ
				// newChat.sendMessage("���ǲ���");

				// ��ȡ������
				Roster roster = connection.getRoster();
				Collection<RosterEntry> entries = roster.getEntries();
				for (RosterEntry entry : entries) {
					System.out.print(entry.getName() + " - " + entry.getUser()
							+ " - " + entry.getType() + " - "
							+ entry.getGroups().size());
					Presence presence = roster.getPresence(entry.getUser());
					System.out.println(" - " + presence.getStatus() + " - "
							+ presence.getFrom());
				}

				// ��ӻ��������������������״̬�ĸı䡣
				roster.addRosterListener(new RosterListener() {

					@Override
					public void entriesAdded(Collection<String> addresses) {
						System.out.println("entriesAdded");
					}

					@Override
					public void entriesUpdated(Collection<String> addresses) {
						System.out.println("entriesUpdated");
					}

					@Override
					public void entriesDeleted(Collection<String> addresses) {
						System.out.println("entriesDeleted");
					}

					@Override
					public void presenceChanged(Presence presence) {
						System.out.println("presenceChanged - >"
								+ presence.getStatus());
					}

				});

				// ������
				// /RosterGroup group = roster.createGroup("��ѧ");
				// for(RosterEntry entry : entries) {
				// group.addEntry(entry);
				// }
				for (RosterGroup g : roster.getGroups()) {
					for (RosterEntry entry : g.getEntries()) {
						System.out.println("Group " + g.getName() + " >> "
								+ entry.getName() + " - " + entry.getUser()
								+ " - " + entry.getType() + " - "
								+ entry.getGroups().size());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.multi_line_list_item, messages);
		mList.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		connection.disconnect();
		System.exit(0);
		super.onDestroy();
	}

	/**
	 * ֱ�ӵ���setImageBitmap���޷�������ʾ�ģ����������ϵ�֪������һ���������߳��в���
	 * 
	 * @author Lynx
	 * 
	 */
	private class setMapThread implements Runnable {
		public Bitmap mBitmap = null;

		public void setB(Bitmap mBitmap) {
			this.mBitmap = mBitmap;
		}

		// run��������UI�߳���ִ��
		public void run() {

			mImageView.setImageBitmap(mBitmap);
			if (D)
				Log.d(TAG, "+++ Jpeg Sitted +++");
		}
	}

	/**
	 * ���ڼ����Ϣ�Ĺ��Ӻ���������������Ⲣִ�п������
	 * 
	 * @param messageIn
	 *            ��Ҫ��������Ϣ
	 */
	private void MessageHook(Message messageIn) {
		if (D)
			Log.d(TAG, "+++ ON HOOK +++");

		DefaultPacketExtension ExtensionGot = (DefaultPacketExtension) messageIn
				.getExtension(EElementName, ENameSpace);
		if (ExtensionGot != null) {

			String timeString = ExtensionGot.getValue(ETimeName);
			long messageTime = 0;

			try {
				messageTime = Long.parseLong(timeString);
			} catch (NumberFormatException e) {
				if (D)
					Log.d(TAG, "+++ Photo Time not Set +++");
				messageTime = 0;
			}

			if (messageTime >= lastPhotoTime || messageTime == 0) // ֻ����ͼƬ��ʱ����ھ�ͼƬʱ����ʾ�����߸ù���û�б�������
			{

				// ���ü�¼��ʱ��
				lastPhotoTime = messageTime;

				String jpegString = ExtensionGot.getValue(EValueName);
				byte[] JpegData = Base64.decode(jpegString);

				if (D)
					Log.d(TAG,
							"+++ Jpeg Found +++" + jpegString.substring(0, 40));

				// ��BitmapFactory.decodeByteArray()�������԰�������ص�������ת����Bitmap����
				Bitmap mBitmap = null;
				mBitmap = BitmapFactory.decodeByteArray(JpegData, 0,
						JpegData.length);

				// ���ݲ�ͬ������������ͼƬ�Ƿ���Ҫ��ת�ʹ洢
				String JpegMessageType = messageIn.getBody();
				if (JpegMessageType.equals("ShotCut-JPEG")) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					Bitmap nbmp = Bitmap.createBitmap(mBitmap, 0, 0,
							mBitmap.getWidth(), mBitmap.getHeight(), matrix,
							true);
					mBitmap = nbmp;
				} else {
					new DateFormat();
					File file = new File("/mnt/sdcard/Car"
							+ DateFormat.format("yyyyMMdd_hhmmss",
									Calendar.getInstance(Locale.CHINA))
							+ ".jpg");
					try {
						file.createNewFile();
						BufferedOutputStream os = new BufferedOutputStream(
								new FileOutputStream(file));
						mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
						os.flush();
						os.close();
						Toast.makeText(getApplicationContext(),
								"ͼƬ������ϣ��ڴ洢���ĸ�Ŀ¼", Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				setMapThread newThread = new setMapThread();
				newThread.setB(mBitmap);
				mImageView.post(newThread);

				// �������Ĺ������ǰ�Bitmap�����һ���洢���е��ļ�

				/*
				 * File file = new File("/mnt/sdcard/YY" + new
				 * DateFormat().format("yyyyMMdd_hhmmss",
				 * Calendar.getInstance(Locale.CHINA)) + ".jpg"); try {
				 * file.createNewFile(); BufferedOutputStream os = new
				 * BufferedOutputStream( new FileOutputStream(file));
				 * mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
				 * os.flush(); os.close();
				 * Toast.makeText(getApplicationContext(), "ͼƬ������ϣ��ڴ洢���ĸ�Ŀ¼",
				 * Toast.LENGTH_LONG).show(); } catch (IOException e) {
				 * e.printStackTrace();
				 * 
				 * }
				 */
			}

		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.main);
			initFindsAndListeners();
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.main);
			initFindsAndListeners();
		}
	}

} 