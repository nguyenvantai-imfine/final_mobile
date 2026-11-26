package com.example.ck_mobile.ui.customer.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ck_mobile.R;
import com.example.ck_mobile.databinding.ItemChatMessageBinding;
import com.example.ck_mobile.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/** T46: bong bóng chat (người dùng căn phải, bot căn trái, bot có thể kèm nút deep link). */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    public interface OnAction {
        void onAction(String target);
    }

    private final List<ChatMessage> items = new ArrayList<>();
    private final OnAction listener;

    public ChatAdapter(OnAction listener) {
        this.listener = listener;
    }

    public void submit(List<ChatMessage> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemChatMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ChatMessage m = items.get(position);
        boolean user = m.isFromUser();
        h.b.tvText.setText(m.getText());
        h.b.tvText.setTextColor(ContextCompat.getColor(h.b.getRoot().getContext(),
                user ? R.color.tdt_on_primary : R.color.tdt_text_primary));
        h.b.bubble.setBackgroundResource(user ? R.drawable.bg_chat_user : R.drawable.bg_chat_bot);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) h.b.bubble.getLayoutParams();
        lp.gravity = user ? Gravity.END : Gravity.START;
        h.b.bubble.setLayoutParams(lp);

        boolean hasAction = !user && m.getActionTarget() != null && m.getActionLabel() != null;
        h.b.btnAction.setVisibility(hasAction ? android.view.View.VISIBLE : android.view.View.GONE);
        if (hasAction) {
            h.b.btnAction.setText(m.getActionLabel());
            h.b.btnAction.setOnClickListener(v -> listener.onAction(m.getActionTarget()));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemChatMessageBinding b;

        VH(ItemChatMessageBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
