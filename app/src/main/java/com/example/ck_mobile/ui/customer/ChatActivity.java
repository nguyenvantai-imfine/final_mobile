package com.example.ck_mobile.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ck_mobile.chat.ChatPrefs;
import com.example.ck_mobile.databinding.ActivityChatBinding;
import com.example.ck_mobile.ui.base.BaseSecureActivity;
import com.example.ck_mobile.ui.customer.adapter.ChatAdapter;
import com.example.ck_mobile.utils.AuthSessionHelper;
import com.example.ck_mobile.utils.DeepLinkRouter;
import com.example.ck_mobile.viewmodel.ChatViewModel;

/** T46 + T47 + T53 + T54: màn hình chatbot (rule-based, LLM, deep link). */
public class ChatActivity extends BaseSecureActivity {

    private ActivityChatBinding b;
    private ChatViewModel vm;
    private ChatAdapter adapter;
    private boolean updatingSwitch;

    @Override
    protected String getRequiredRole() {
        return AuthSessionHelper.ROLE_CUSTOMER;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.toolbar.setNavigationOnClickListener(v -> finish());

        vm = new ViewModelProvider(this).get(ChatViewModel.class);

        adapter = new ChatAdapter(target -> DeepLinkRouter.open(this, target));
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        b.rvMessages.setLayoutManager(lm);
        b.rvMessages.setAdapter(adapter);

        vm.getMessages().observe(this, list -> {
            adapter.submit(list);
            if (!list.isEmpty()) b.rvMessages.scrollToPosition(list.size() - 1);
        });
        vm.getTyping().observe(this, t -> {
            boolean typing = Boolean.TRUE.equals(t);
            b.tvTyping.setVisibility(typing ? View.VISIBLE : View.GONE);
            b.btnSend.setEnabled(!typing);
        });
        vm.getMode().observe(this, mode -> {
            boolean llm = ChatPrefs.MODE_LLM.equals(mode);
            updatingSwitch = true;
            b.switchAi.setChecked(llm);
            updatingSwitch = false;
            b.tvMode.setText(llm ? "Chế độ: Trợ lý AI (Gemini), tự về trả lời tự động khi lỗi"
                    : "Chế độ: Trả lời tự động (ổn định, không cần mạng ngoài)");
        });

        b.switchAi.setOnCheckedChangeListener((v, checked) -> {
            if (updatingSwitch) return;
            if (checked && !vm.isLlmAvailable()) {
                updatingSwitch = true;
                b.switchAi.setChecked(false);
                updatingSwitch = false;
                Toast.makeText(this, "Chưa cấu hình GEMINI_API_KEY trong local.properties.", Toast.LENGTH_LONG).show();
                return;
            }
            vm.setLlmMode(checked);
        });

        b.btnSend.setOnClickListener(v -> sendCurrent());
        b.etMessage.setOnEditorActionListener((tv, actionId, ev) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrent();
                return true;
            }
            return false;
        });

        vm.loadHistory();
    }

    private void sendCurrent() {
        String text = b.etMessage.getText() == null ? "" : b.etMessage.getText().toString();
        if (text.trim().isEmpty()) return;
        b.etMessage.setText("");
        vm.send(text);
    }
}
