package io.mrarm.irc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServerListFragment extends Fragment {

    public static ServerListFragment newInstance() {
        return new ServerListFragment();
    }

    private ServerListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server_list, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((MainActivity) getActivity()).addActionBarDrawerToggle(toolbar);

        FloatingActionButton addFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        addFab.setOnClickListener((View view) -> {
            startActivity(new Intent(getContext(), EditServerActivity.class));
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.server_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new ServerListAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        mAdapter.setActiveServerClickListener((ServerConnectionInfo info) -> {
            ((MainActivity) getActivity()).openServer(info, null);
        });
        mAdapter.setInactiveServerClickListener((ServerConfigData data) -> {
            ServerConnectionManager.getInstance().createConnection(data);
        });
        mAdapter.setInactiveServerLongClickListener((ServerConfigData data) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(data.name);
            builder.setItems(new CharSequence[] {
                    getString(R.string.action_edit),
                    getString(R.string.action_delete)
            }, (DialogInterface dialog, int which) -> {
                if (which == 0) { // edit
                    startActivity(EditServerActivity.getLaunchIntent(getContext(), data));
                } else if (which == 1) { // delete
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                    builder2.setTitle(R.string.action_delete_confirm_title);
                    builder2.setMessage(getString(R.string.action_delete_confirm_body, data.name));
                    builder2.setPositiveButton(R.string.action_delete, (DialogInterface dialog2, int which2) -> {
                        ServerConfigManager.getInstance(getContext()).deleteServer(data);
                    });
                    builder2.setNegativeButton(R.string.action_cancel, null);
                    builder2.show();
                }
            });
            builder.show();
        });
        mAdapter.registerListeners();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (mAdapter != null)
            mAdapter.unregisterListeners();
        super.onDestroyView();
    }
}
