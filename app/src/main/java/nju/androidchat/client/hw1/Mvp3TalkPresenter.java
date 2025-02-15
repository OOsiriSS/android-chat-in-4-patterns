package nju.androidchat.client.hw1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp3TalkPresenter implements Mvp3Contract.TalkPresenter {

    private Mvp3Contract.TalkModel mvp3TalkModel;
    private Mvp3Contract.TalkView iMvp3TalkView;

    private List<ClientMessage> clientMessages;

    @Override
    public void sendMessage(String content) {
        ClientMessage clientMessage = mvp3TalkModel.sendInformation(content);
        refreshMessageList(clientMessage);
    }

    @Override
    public void receiveMessage(ClientMessage clientMessage) {
        refreshMessageList(clientMessage);
    }

    @Override
    public String getUsername() {
        return mvp3TalkModel.getUsername();
    }

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp3TalkView.showMessageList(clientMessages);
    }

    //撤回消息，Mvp0暂不实现
    @Override
    public void recallMessage(UUID messageId) {
        // 操作界面
        List<ClientMessage> newMessages = new ArrayList<>();
        for (ClientMessage clientMessage : clientMessages) {
            if (clientMessage.getMessageId().equals(messageId)) {
                newMessages.add(new ClientMessage(clientMessage.getMessageId(), clientMessage.getTime(), clientMessage.getSenderUsername(), "(已撤回)"));
            } else {
                newMessages.add(clientMessage);
            }
        }
        this.clientMessages = newMessages;
        this.iMvp3TalkView.showMessageList(newMessages);

        // 操作数据
        this.mvp3TalkModel.recallMessage(messageId);

    }

    @Override
    public void start() {

    }
}
