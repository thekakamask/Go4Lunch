package com.dcac.go4lunch.viewModel;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.chat.Message;
import com.dcac.go4lunch.repository.ChatRepository;
import com.dcac.go4lunch.utils.LiveDataTestUtils;
import com.dcac.go4lunch.viewModels.ChatViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChatViewModelUnitTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public ChatRepository mockChatRepository;

    @Mock
    public Query mockQueryAllMessage;

    private ChatViewModel SUT;

    private final MutableLiveData<Query> liveDataMessages = new MutableLiveData<>();
    private final String chatId = "testChatId";


    @Before
    public void setup() {
        doReturn(liveDataMessages)
                .when(mockChatRepository)
                .getMessagesLiveData(chatId);

        SUT = new ChatViewModel(mockChatRepository);
    }

    @Test
    public void testGetAllMessagesForChatSuccess() throws InterruptedException {
        // Arrange
        liveDataMessages.setValue(mockQueryAllMessage);

        // Act
        Query result = LiveDataTestUtils.getOrAwaitValue(SUT.getAllMessagesForChat(chatId));

        // Assert
        verify(mockChatRepository).getMessagesLiveData(chatId);
        assert(result == mockQueryAllMessage);
    }

    @Test
    public void testGetAllMessagesForChatFailure() throws InterruptedException {
        // Arrange
        liveDataMessages.setValue(null);

        // Act
        Query result = LiveDataTestUtils.getOrAwaitValue(SUT.getAllMessagesForChat(chatId));

        // Assert
        verify(mockChatRepository).getMessagesLiveData(chatId);
        assert(result == null);
    }

    @Test
    public void testSendMessageSuccess() {
        // Arrange
        Message message = new Message();

        // Act
        SUT.sendMessage(chatId, message);

        // Assert
        verify(mockChatRepository).sendMessage(chatId, message);
    }

    @Test
    public void testSendMessageFailure() {
        // Arrange
        Message message = new Message();
        doNothing().when(mockChatRepository).sendMessage(chatId, message);

        // Act
        SUT.sendMessage(chatId, message);

        // Assert
        verify(mockChatRepository).sendMessage(chatId, message);
    }
}
