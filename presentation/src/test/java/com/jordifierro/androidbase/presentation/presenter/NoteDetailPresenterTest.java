package com.jordifierro.androidbase.presentation.presenter;

import com.jordifierro.androidbase.data.net.error.RestApiErrorException;
import com.jordifierro.androidbase.domain.entity.NoteEntity;
import com.jordifierro.androidbase.domain.interactor.note.DeleteNoteUseCase;
import com.jordifierro.androidbase.domain.interactor.note.GetNoteUseCase;
import com.jordifierro.androidbase.presentation.view.NoteDetailView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

public class NoteDetailPresenterTest {

    @Mock GetNoteUseCase getNoteUseCase;
    @Mock DeleteNoteUseCase deleteNoteUseCase;
    @Mock NoteDetailView mockNoteDetailView;
    @Mock Observable mockObservable;

    private NoteDetailPresenter noteDetailPresenter;
    private NoteDetailPresenter.NoteDetailSubscriber noteDetailSubscriber;
    private NoteDetailPresenter.NoteDeleteSubscriber noteDeleteSubscriber;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.noteDetailPresenter =
                new NoteDetailPresenter(this.getNoteUseCase, this.deleteNoteUseCase);
        this.noteDetailPresenter.initWithView(this.mockNoteDetailView);
        this.noteDetailSubscriber = this.noteDetailPresenter.new NoteDetailSubscriber();
        this.noteDeleteSubscriber = this.noteDetailPresenter.new NoteDeleteSubscriber();
    }

    @Test
    public void testDestroy() {

        this.noteDetailPresenter.destroy();

        verify(this.getNoteUseCase).unsubscribe();
        assertNull(this.noteDetailPresenter.noteDetailView);
        assertNull(this.noteDetailPresenter.view);
    }

    @Test
    public void testGetNote() throws Exception {

        this.noteDetailPresenter.resume();

        verify(this.mockNoteDetailView).getNoteId();
        verify(this.mockNoteDetailView).showLoader();
        verify(this.getNoteUseCase).setParams(any(int.class));
        verify(this.getNoteUseCase).execute(any(BasePresenter.BaseSubscriber.class));
    }

    @Test
    public void testSubscriberOnCompleted() {

        this.noteDetailSubscriber.onCompleted();

        verify(this.mockNoteDetailView).hideLoader();
    }

    @Test
    public void testSubscriberOnError() {

        this.noteDetailSubscriber.onError(new RestApiErrorException("Error message", 500));

        verify(this.mockNoteDetailView).hideLoader();
        verify(this.mockNoteDetailView).showError(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSubscriberOnNext() {

        this.noteDetailSubscriber.onNext(new NoteEntity(1, "", ""));

        verify(this.mockNoteDetailView).hideLoader();
        verify(this.mockNoteDetailView).showNote(any(NoteEntity.class));
    }

    @Test
    public void testEditNoteButtonPressed() {

        this.noteDetailPresenter.editNoteButtonPressed();

        verify(this.mockNoteDetailView).navigateToEdit();
    }

    @Test
    public void testDeleteNoteButtonPressed() {

        this.noteDetailPresenter.deleteNoteButtonPressed();

        verify(this.mockNoteDetailView).getNoteId();
        verify(this.deleteNoteUseCase).setParams(anyInt());
        verify(this.deleteNoteUseCase).execute(any(NoteDetailPresenter.NoteDeleteSubscriber.class));
    }

    @Test
    public void testDeleteSubscriberOnCompleted() {

        this.noteDeleteSubscriber.onCompleted();

        verify(this.mockNoteDetailView).hideLoader();
    }

    @Test
    public void testDeleteSubscriberOnError() {

        this.noteDeleteSubscriber.onError(new RestApiErrorException("Error message", 500));

        verify(this.mockNoteDetailView).hideLoader();
        verify(this.mockNoteDetailView).showError(any(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteSubscriberOnNext() {

        this.noteDeleteSubscriber.onNext(null);

        verify(this.mockNoteDetailView).hideLoader();
        verify(this.mockNoteDetailView).close();
    }

}
