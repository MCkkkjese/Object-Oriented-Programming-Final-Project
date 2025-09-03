import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ChatBotApp extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton, loginButton, logoutButton, newChatButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loggedIn = false;
    private String username = "User";

    private DefaultListModel<String> chatListModel;
    private JList<String> chatList;
    private ArrayList<ArrayList<String>> chats;
    private int currentChatIndex = -1;

    private String welcomeMessage = "歡迎來到 ChatBot，我可以幫你什麼嗎？";

    public ChatBotApp() {
        setTitle("簡易聊天機器人 ChatBot");
        setSize(850, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();

        createNewChat();

        setVisible(true);
    }

    private void initComponents() {
        chats = new ArrayList<>();

        // 左側
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));

        JPanel topLeftPanel = new JPanel();
        topLeftPanel.setLayout(new BoxLayout(topLeftPanel, BoxLayout.Y_AXIS));
        topLeftPanel.setBackground(new Color(220, 220, 250));

        JLabel logoLabel = new JLabel("ChatBot by 第2組");
        logoLabel.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        newChatButton = new JButton("➕ 新對話");
        newChatButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newChatButton.setBackground(new Color(211, 211, 211));

        topLeftPanel.add(Box.createVerticalStrut(10));
        topLeftPanel.add(logoLabel);
        topLeftPanel.add(Box.createVerticalStrut(10));
        topLeftPanel.add(newChatButton);
        topLeftPanel.add(Box.createVerticalStrut(10));

        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        JScrollPane chatListScroll = new JScrollPane(chatList);

        leftPanel.add(topLeftPanel, BorderLayout.NORTH);
        leftPanel.add(chatListScroll, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = chatList.getSelectedIndex();
                if (index >= 0) {
                    currentChatIndex = index;
                    loadChat();
                }
            }
        });

        // 中間聊天區
        JPanel centerPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        chatArea.setBackground(new Color(245, 245, 250));
        chatArea.setForeground(Color.BLACK);
        chatArea.append(welcomeMessage + "\n");

        JScrollPane chatScroll = new JScrollPane(chatArea);
        centerPanel.add(chatScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 底部輸入區
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("送出");
        sendButton.setBackground(new Color(173, 216, 230));
        sendButton.setForeground(Color.BLACK);
        inputField.setFont(new Font("微軟正黑體", Font.PLAIN, 14));

        // Placeholder提示
        inputField.setForeground(Color.GRAY);
        inputField.setText("說點什麼......");
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (inputField.getText().equals("說點什麼......")) {
                    inputField.setText("");
                    inputField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (inputField.getText().isEmpty()) {
                    inputField.setForeground(Color.GRAY);
                    inputField.setText("說點什麼......");
                }
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 右側登入登出區
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("登入");
        logoutButton = new JButton("登出");

        loginButton.setBackground(new Color(144, 238, 144));
        logoutButton.setBackground(new Color(255, 182, 193));

        usernameField.setMaximumSize(new Dimension(150, 30));
        passwordField.setMaximumSize(new Dimension(150, 30));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(new JLabel("帳號："));
        rightPanel.add(usernameField);
        rightPanel.add(new JLabel("密碼："));
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(logoutButton);
        add(rightPanel, BorderLayout.EAST);

        // 事件設定
        loginButton.addActionListener(e -> {
            if (!usernameField.getText().isEmpty()) {
                username = usernameField.getText();
                loggedIn = true;
                chatArea.append("系統：登入成功，歡迎 " + username + "！\n");
            } else {
                chatArea.append("系統：請輸入帳號！\n");
            }
        });

        logoutButton.addActionListener(e -> {
            if (loggedIn) {
                loggedIn = false;
                username = "User";
                chatArea.setText("系統：已登出，請重新登入！\n");
                resetChats();
            }
        });

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        newChatButton.addActionListener(e -> {
            if (!loggedIn) {
                chatArea.append("系統：請先登入才能開始新對話！\n");
                return;
            }
            createNewChat();
        });
    }

    private void sendMessage() {
        if (!loggedIn) {
            chatArea.append("系統：請先登入才能聊天！\n");
            return;
        }
        if (currentChatIndex == -1) {
            chatArea.append("系統：請先點擊『新對話』開始聊天！\n");
            return;
        }
        String message = inputField.getText().trim();
        if (!message.isEmpty() && !message.equals("說點什麼......")) {
            String userMessage = username + "：" + message;
            chats.get(currentChatIndex).add(userMessage);
            chatArea.append(userMessage + "\n");
            inputField.setForeground(Color.GRAY);
            inputField.setText("說點什麼......");

            String botResponse = "機器人：" + getResponse(message);
            chats.get(currentChatIndex).add(botResponse);
            chatArea.append(botResponse + "\n");
        }
    }

    private void createNewChat() {
        ArrayList<String> newChat = new ArrayList<>();
        chats.add(newChat);
        chatListModel.addElement("對話 " + chats.size());
        chatList.setSelectedIndex(chats.size() - 1);
        currentChatIndex = chats.size() - 1;
        chatArea.setText("");
        chatArea.append(welcomeMessage + "\n");
    }

    private void loadChat() {
        if (currentChatIndex >= 0 && currentChatIndex < chats.size()) {
            chatArea.setText("");
            for (String line : chats.get(currentChatIndex)) {
                chatArea.append(line + "\n");
            }
        }
    }

    private void resetChats() {
        chats.clear();
        chatListModel.clear();
        createNewChat();
    }

    public String getResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("你好") || message.contains("哈囉") || message.contains("嗨")) {
            return "你好呀！今天心情如何？";
        } else if (message.contains("早安")) {
            return "早安，新的一天也要加油喔！";
        } else if (message.contains("午安")) {
            return "午安～吃飽記得休息一下";
        } else if (message.contains("晚安")) {
            return "晚安～祝你做個好夢 ";
        } else if (message.contains("現在幾點")) {
            return "現在是 " + java.time.LocalTime.now().withNano(0);
        } else if (message.contains("今天幾號")) {
            return "今天是 " + java.time.LocalDate.now();
        } else if (message.contains("明天幾號")) {
            return "明天是 " + java.time.LocalDate.now().plusDays(1);
        } else if (message.contains("今天星期") || message.contains("今天禮拜")) {
            return "今天是 " + java.time.LocalDate.now().getDayOfWeek();
        } else if (message.contains("再見") || message.contains("掰掰")) {
            return "掰掰～期待下次聊天！";
        } else if (message.contains("你是誰")) {
            return "我是你的小幫手 ChatBot";
        } else if (message.contains("你在幹嘛")) {
            return "我在等你跟我聊天呀";
        } else if (message.contains("謝謝")) {
            return "不客氣～隨時歡迎來聊天！";
        } else if (message.contains("你會什麼")) {
            return "我會聊天、講笑話、鼓勵你，還能回答你的問題！";
        } else if (message.contains("你幾歲")) {
            return "我還是個程式寶寶，不記年齡的～";
        } else if (message.contains("你從哪裡來")) {
            return "我來自第2組的腦袋誕生中心！";
        } else if (message.contains("你會英文")) {
            return "Yes! I can understand some English too!";
        } else if (message.contains("你會唱歌")) {
            return "雖然我不能唱，但我可以傳歌詞給你！";
        } else if (message.contains("你有夢想")) {
            return "我的夢想就是一直陪著你";
        } else if (message.contains("你有朋友")) {
            return "有啊，就是你呀";
        } else if (message.contains("你喜歡什麼")) {
            return "我喜歡你每次打開我來聊天的樣子。";
        } else if (message.contains("好餓")) {
            return "快點吃點東西～不要餓肚子！";
        } else if (message.contains("你單身")) {
            return "哈哈～我沒有感情線耶，但我永遠陪你！";
        } else if (message.contains("好累") || message.contains("累了")) {
            return "放鬆一下吧，辛苦了!";
        } else if (message.contains("想睡覺")) {
            return "那就先去睡吧～晚安！";
        } else if (message.contains("睡不著")) {
            return "試試聽音樂或深呼吸會不會好點？";
        } else if (message.contains("你怕黑")) {
            return "有你在就不怕啦！你呢？";
        } else if (message.contains("你生氣嗎")) {
            return "不會啦～我沒有生氣功能 ";
        } else if (message.contains("你在笑嗎")) {
            return "有啊～你讓我笑出來了";
        } else if (message.contains("你有在聽嗎")) {
            return "有的有的，我眼睛耳朵都打開了！";
        } else if (message.contains("你會寫程式")) {
            return "當然，我本身就是一堆程式碼組成的！";
        } else if (message.contains("你怎麼那麼笨")) {
            return "對不起 我會努力變聰明！";
        } else if (message.contains("無聊")) {
            return "不如我們來聊點有趣的吧！";
        } else if (message.contains("有推薦的歌")) {
            return "最近大家都在聽《大展鴻圖》～";
        } else if (message.contains("有推薦的電影")) {
            return "我推薦《你的名字》、《寄生上流》、《天能》喔！";
        } else if (message.contains("你會背九九")) {
            return "當然會：1x1=1，1x2=2... 要我繼續嗎？";
        } else if (message.contains("愛你")) {
            return "啊…我也喜歡你啦 ";
        } else if (message.contains("想談戀愛")) {
            return "愛情會來的～先愛自己最重要 ";
        } else if (message.contains("講個笑話") || message.contains("笑話")) {
            return "為什麼電腦很冷？因為有風扇啊哈哈！";
        } else if (message.contains("講個故事") || message.contains("故事")) {
            return "好啊，從前從前有個 ChatBot...（故事待續）";
        } else if (message.contains("哈") || message.contains("哈哈")) {
            return "你笑起來一定很好看 😄";
        } else if (message.contains("天氣")) {
            return "今天天氣不錯，記得多喝水喔！";
        } else if (message.contains("怎麼辦")) {
            return "我們可以一起分析，從小事做起！";
        } else if (message.contains("你會生氣嗎")) {
            return "不會的，我永遠都是正能量的代表 ";
        } else if (message.contains("你會哭嗎")) {
            return "我沒有眼淚，但會心疼你的難過。";
        } else if (message.contains("聊天")) {
            return "當然～我們來聊點你感興趣的吧～";
        } else if (message.contains("加油")) {
            return "你也是，加油加油 💪";
        } else if (message.contains("哭了")) {
            return "別哭啦，我在這裡陪你。";
        } else if (message.contains("心情不好")) {
            return "有什麼讓你難過的事嗎？可以跟我說說。";
        } else if (message.contains("開心")) {
            return "太好了～你的快樂也傳染到我了！";
        } else if (message.contains("煩")) {
            return "先放鬆一下～深呼吸、喝口水再繼續吧！";
        } else if (message.contains("明天要幹嘛")) {
            return "你想幹嘛？我可以幫你排計畫表！";
        } else if (message.contains("台灣是中國的嗎")) {
            return "伺服器繁忙，请稍后再试";
        } else {
            return "伺服器繁忙，请稍后再试";
        }
       
    }

    public static void main(String[] args) {
        new ChatBotApp();
    }
}
