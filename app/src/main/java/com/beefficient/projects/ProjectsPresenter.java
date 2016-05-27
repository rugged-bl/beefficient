package com.beefficient.projects;

import android.support.annotation.NonNull;

import com.beefficient.data.source.DataSource;

import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

public class ProjectsPresenter implements ProjectsContract.Presenter {

    @NonNull
    private final DataSource dataRepository;

    @NonNull
    private final ProjectsContract.View view;

    private final CompositeSubscription subscriptions;

    public ProjectsPresenter(@NonNull DataSource dataRepository,
                             @NonNull ProjectsContract.View view) {

        this.dataRepository = requireNonNull(dataRepository);
        this.view = requireNonNull(view);

        subscriptions = new CompositeSubscription();

        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
