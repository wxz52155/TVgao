package com.fongmi.android.tv.ui.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.fongmi.android.tv.bean.Track;
import com.fongmi.android.tv.databinding.DialogTrackBinding;
import com.fongmi.android.tv.player.Players;
import com.fongmi.android.tv.ui.adapter.TrackAdapter;
import com.fongmi.android.tv.utils.ResUtil;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public final class TrackSelectionDialog implements TrackAdapter.OnClickListener {

    private final DialogTrackBinding binding;
    private final TrackNameProvider provider;
    private final TrackAdapter adapter;
    private final AlertDialog dialog;
    private Players player;
    private int type;

    public static TrackSelectionDialog create(Activity activity) {
        return new TrackSelectionDialog(activity);
    }

    public TrackSelectionDialog(Activity activity) {
        this.binding = DialogTrackBinding.inflate(LayoutInflater.from(activity));
        this.dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).create();
        this.adapter = new TrackAdapter(this);
        this.provider = new TrackNameProvider();
    }

    public TrackSelectionDialog type(int type) {
        this.type = type;
        return this;
    }

    public TrackSelectionDialog player(Players player) {
        this.player = player;
        return this;
    }

    public void show() {
        setRecyclerView();
        setDialog();
    }

    private void setRecyclerView() {
        binding.recycler.setHasFixedSize(true);
        binding.recycler.addItemDecoration(new SpaceItemDecoration(1, 16));
        binding.recycler.setAdapter(adapter.addAll(getTrack()));
        binding.recycler.scrollToPosition(adapter.getSelected());
    }

    private void setDialog() {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (ResUtil.getScreenWidthPx() * 0.4f);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setDimAmount(0);
        dialog.show();
    }

    private List<Track> getTrack() {
        List<Track> items = new ArrayList<>();
        if (player.isExo()) addExoTrack(items);
        if (player.isIjk()) addIjkTrack(items);
        return items;
    }

    private void addExoTrack(List<Track> items) {
        List<Tracks.Group> groups = player.exo().getCurrentTracks().getGroups();
        for (int i = 0; i < groups.size(); i++) {
            Tracks.Group trackGroup = groups.get(i);
            if (trackGroup.getType() != type) continue;
            for (int j = 0; j < trackGroup.length; j++) {
                Track item = new Track(provider.getTrackName(trackGroup.getTrackFormat(j)));
                item.setSelected(trackGroup.isTrackSelected(j));
                item.setGroup(i);
                item.setTrack(j);
                items.add(item);
            }
        }
    }

    private void addIjkTrack(List<Track> items) {
        int track = player.ijk().getSelectedTrack(type);
        IjkTrackInfo[] trackInfos = player.ijk().getTrackInfo();
        for (int i = 0; i < trackInfos.length; i++) {
            IjkTrackInfo trackInfo = trackInfos[i];
            if (trackInfo.getTrackType() != type) continue;
            Track item = new Track(provider.getTrackName(trackInfo));
            item.setSelected(track == i);
            item.setTrack(i);
            items.add(item);
        }
    }

    @Override
    public void onItemClick(Track item) {
        if (player.isExo()) player.exo().setTrackSelectionParameters(player.exo().getTrackSelectionParameters().buildUpon().setOverrideForType(new TrackSelectionOverride(player.exo().getCurrentTracks().getGroups().get(item.getGroup()).getMediaTrackGroup(), item.getTrack())).build());
        if (player.isIjk()) player.ijk().selectTrack(item.getTrack());
        dialog.dismiss();
    }
}