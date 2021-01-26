package com.example.germanexam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.core.content.FileProvider.getUriForFile;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.FileManagerViewHolder> {

    List<String[]> database;
    Context context;
    int whoPlayed = -1;
    MediaPlayer player = null;

    FileManagerAdapter(List<String[]> files, Context context) {
        database = files;
        this.context = context;
    }

    @NonNull
    @Override
    public FileManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.files_family, parent, false);

        return new FileManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileManagerViewHolder holder, final int position) {
        final String[] files = database.get(position);
        String pathToFile = files[0];
        File file = new File(pathToFile);
        String fileNameWithMp3 = file.getName();
        String fileName = fileNameWithMp3.substring(0, fileNameWithMp3.length() - 4);
        String[] words = fileName.split("_");
        holder.title.setText(words[0] + " " + words[1] + " " + words[2] + " вариант " + words[5]);

        changeAllTimeRemaining(holder, files);

        holder.playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoPlayed == -1 || whoPlayed == position) {
                    whoPlayed = position;
                    if (holder.playButton2Pressed || holder.playButton3Pressed || holder.playButton4Pressed) {
                        holder.playButton2Pressed = false;
                        holder.playButton3Pressed = false;
                        holder.playButton4Pressed = false;
                        holder.countDownTimer.cancel();
                        stopPlaying();
                    }
                    holder.playButton1Pressed = !holder.playButton1Pressed;
                    holder.fileName = files[0];
                    if (holder.playButton1Pressed) {
                        startPlaying(holder);
                        holder.timeLeft1 = player.getDuration();
                        holder.progressBar1.setMax((int) (holder.timeLeft1 / 1000) + 1);
                        holder.countDownTimer = new CountDownTimer(holder.timeLeft1, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (whoPlayed == -1) {
                                    holder.countDownTimer.cancel();
                                    resetAllButtons(holder);
                                    stopPlaying();
                                    return;
                                }
                                holder.timeLeft1 = millisUntilFinished;
                                updateTimer(holder.timeLeft1, holder.timeRemaining1);
                                holder.counter1++;
                                holder.progressBar1.setProgress(holder.counter1);
                            }

                            @Override
                            public void onFinish() {
                                holder.countDownTimer.cancel();
                                whoPlayed = -1;
                                holder.playButton1Pressed = false;
                                holder.progressBar1.setProgress(0);
                                holder.timeRemaining1.setText(holder.times[0]);
                                holder.counter1 = 0;
                                stopPlaying();
                                changeButtons(holder);
                            }
                        }.start();
                    } else {
                        holder.countDownTimer.cancel();
                        whoPlayed = -1;
                        holder.progressBar1.setProgress(0);
                        holder.timeRemaining1.setText(holder.times[0]);
                        holder.counter1 = 0;
                        stopPlaying();
                    }
                    changeButtons(holder);
                } else {
                    Toast.makeText(holder.title.getContext(), "Воспроизводится другой вариант", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoPlayed == -1 || whoPlayed == position) {
                    whoPlayed = position;
                    if (holder.playButton1Pressed || holder.playButton3Pressed || holder.playButton4Pressed) {
                        holder.playButton1Pressed = false;
                        holder.playButton3Pressed = false;
                        holder.playButton4Pressed = false;
                        holder.countDownTimer.cancel();
                        stopPlaying();
                    }
                    holder.playButton2Pressed = !holder.playButton2Pressed;
                    holder.fileName = files[1];
                    if (holder.playButton2Pressed) {
                        startPlaying(holder);
                        holder.timeLeft2 = player.getDuration();
                        holder.progressBar2.setMax((int) (holder.timeLeft2 / 1000) + 1);
                        holder.countDownTimer = new CountDownTimer(holder.timeLeft2, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (whoPlayed == -1) {
                                    holder.countDownTimer.cancel();
                                    resetAllButtons(holder);
                                    stopPlaying();
                                    return;
                                }
                                holder.timeLeft2 = millisUntilFinished;
                                updateTimer(holder.timeLeft2, holder.timeRemaining2);
                                holder.counter2++;
                                holder.progressBar2.setProgress(holder.counter2);
                            }

                            @Override
                            public void onFinish() {
                                holder.countDownTimer.cancel();
                                whoPlayed = -1;
                                holder.playButton2Pressed = false;
                                holder.progressBar2.setProgress(0);
                                holder.timeRemaining2.setText(holder.times[1]);
                                holder.counter2 = 0;
                                stopPlaying();
                                changeButtons(holder);
                            }
                        }.start();
                    } else {
                        holder.countDownTimer.cancel();
                        whoPlayed = -1;
                        holder.progressBar2.setProgress(0);
                        holder.timeRemaining2.setText(holder.times[1]);
                        holder.counter2 = 0;
                        stopPlaying();
                    }
                    changeButtons(holder);
                } else {
                    Toast.makeText(holder.title.getContext(), "Воспроизводится другой вариант", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoPlayed == -1 || whoPlayed == position) {
                    whoPlayed = position;
                    if (holder.playButton1Pressed || holder.playButton2Pressed || holder.playButton4Pressed) {
                        holder.playButton1Pressed = false;
                        holder.playButton2Pressed = false;
                        holder.playButton4Pressed = false;
                        holder.countDownTimer.cancel();
                        stopPlaying();
                    }
                    holder.playButton3Pressed = !holder.playButton3Pressed;
                    holder.fileName = files[2];
                    if (holder.playButton3Pressed) {
                        startPlaying(holder);
                        holder.timeLeft3 = player.getDuration();
                        holder.progressBar3.setMax((int) (holder.timeLeft3 / 1000) + 1);
                        holder.countDownTimer = new CountDownTimer(holder.timeLeft3, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (whoPlayed == -1) {
                                    holder.countDownTimer.cancel();
                                    resetAllButtons(holder);
                                    stopPlaying();
                                    return;
                                }
                                holder.timeLeft3 = millisUntilFinished;
                                updateTimer(holder.timeLeft3, holder.timeRemaining3);
                                holder.counter3++;
                                holder.progressBar3.setProgress(holder.counter3);
                            }

                            @Override
                            public void onFinish() {
                                holder.countDownTimer.cancel();
                                whoPlayed = -1;
                                holder.playButton3Pressed = false;
                                holder.progressBar3.setProgress(0);
                                holder.timeRemaining3.setText(holder.times[2]);
                                holder.counter3 = 0;
                                stopPlaying();
                                changeButtons(holder);
                            }
                        }.start();
                    } else {
                        holder.countDownTimer.cancel();
                        whoPlayed = -1;
                        holder.progressBar3.setProgress(0);
                        holder.timeRemaining3.setText(holder.times[2]);
                        holder.counter3 = 0;
                        stopPlaying();
                    }
                    changeButtons(holder);
                } else {
                    Toast.makeText(holder.title.getContext(), "Воспроизводится другой вариант", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whoPlayed == -1 || whoPlayed == position) {
                    whoPlayed = position;
                    if (holder.playButton1Pressed || holder.playButton2Pressed || holder.playButton3Pressed) {
                        holder.playButton1Pressed = false;
                        holder.playButton2Pressed = false;
                        holder.playButton3Pressed = false;
                        holder.countDownTimer.cancel();
                        stopPlaying();
                    }
                    holder.playButton4Pressed = !holder.playButton4Pressed;
                    holder.fileName = files[3];
                    if (holder.playButton4Pressed) {
                        startPlaying(holder);
                        holder.timeLeft4 = player.getDuration();
                        holder.progressBar4.setMax((int) (holder.timeLeft4 / 1000) + 1);
                        holder.countDownTimer = new CountDownTimer(holder.timeLeft4, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (whoPlayed == -1) {
                                    holder.countDownTimer.cancel();
                                    resetAllButtons(holder);
                                    stopPlaying();
                                    return;
                                }
                                holder.timeLeft4 = millisUntilFinished;
                                updateTimer(holder.timeLeft4, holder.timeRemaining4);
                                holder.counter4++;
                                holder.progressBar4.setProgress(holder.counter4);
                            }

                            @Override
                            public void onFinish() {
                                holder.countDownTimer.cancel();
                                whoPlayed = -1;
                                holder.playButton4Pressed = false;
                                holder.progressBar4.setProgress(0);
                                holder.timeRemaining4.setText(holder.times[3]);
                                holder.counter4 = 0;
                                stopPlaying();
                                changeButtons(holder);
                            }
                        }.start();
                    } else {
                        holder.countDownTimer.cancel();
                        whoPlayed = -1;
                        holder.progressBar4.setProgress(0);
                        holder.timeRemaining4.setText(holder.times[3]);
                        holder.counter4 = 0;
                        stopPlaying();
                    }
                    changeButtons(holder);
                } else {
                    Toast.makeText(holder.title.getContext(), "Воспроизводится другой вариант", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File audio1 = new File(files[0]);
                File audio2 = new File(files[1]);
                File audio3 = new File(files[2]);
                File audio4 = new File(files[3]);
                Uri audioUri1 = getUriForFile(context, "com.example.germanexam.fileprovider", audio1);
                Uri audioUri2 = getUriForFile(context, "com.example.germanexam.fileprovider", audio2);
                Uri audioUri3 = getUriForFile(context, "com.example.germanexam.fileprovider", audio3);
                Uri audioUri4 = getUriForFile(context, "com.example.germanexam.fileprovider", audio4);

                ArrayList<Uri> imageUris = new ArrayList<>();
                imageUris.add(audioUri1);
                imageUris.add(audioUri2);
                imageUris.add(audioUri3);
                imageUris.add(audioUri4);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                shareIntent.setType("video/mp4");

                Intent chooser = Intent.createChooser(shareIntent, "Share File");
                List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    for (Uri uri : imageUris) {
                        context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                context.startActivity(chooser);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFiles(files);
                System.out.println(position);
                database.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, database.size());
                if (database.size() == 0) {
                    Intent intent = new Intent(context, FilesNotFound.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    private  void deleteFiles(String[] files) {
        File file1 = new File(files[0]);
        boolean deleted1 = file1.delete();
        Log.i("FileManager", "Audio1 is deleting:" + deleted1);

        File file2 = new File(files[1]);
        boolean deleted2 = file2.delete();
        Log.i("FileManager", "Audio2 is deleting:" + deleted2);

        File file3 = new File(files[2]);
        boolean deleted3 = file3.delete();
        Log.i("FileManager", "Audio3 is deleting:" + deleted3);

        File file4 = new File(files[3]);
        boolean deleted4 = file4.delete();
        Log.i("FileManager", "Audio4 is deleting:" + deleted4);
    }

    private void changeButtons(FileManagerViewHolder holder) {
        if (holder.playButton1Pressed) {
            holder.playButton1.setBackground(holder.playButton1.getResources().getDrawable(R.drawable.button_red_circle));
            holder.playButton1.setText("\u25A0");
        } else {
            holder.playButton1.setBackground(holder.playButton1.getResources().getDrawable(R.drawable.button_blue_circle));
            holder.playButton1.setText("▶");
            holder.progressBar1.setProgress(0);
            holder.timeRemaining1.setText(holder.times[0]);
            holder.counter1 = 0;
        }
        if (holder.playButton2Pressed) {
            holder.playButton2.setBackground(holder.playButton2.getResources().getDrawable(R.drawable.button_red_circle));
            holder.playButton2.setText("\u25A0");
        } else {
            holder.playButton2.setBackground(holder.playButton2.getResources().getDrawable(R.drawable.button_blue_circle));
            holder.playButton2.setText("▶");
            holder.progressBar2.setProgress(0);
            holder.timeRemaining2.setText(holder.times[1]);
            holder.counter2 = 0;
        }
        if (holder.playButton3Pressed) {
            holder.playButton3.setBackground(holder.playButton3.getResources().getDrawable(R.drawable.button_red_circle));
            holder.playButton3.setText("\u25A0");
        } else {
            holder.playButton3.setBackground(holder.playButton3.getResources().getDrawable(R.drawable.button_blue_circle));
            holder.playButton3.setText("▶");
            holder.progressBar3.setProgress(0);
            holder.timeRemaining3.setText(holder.times[2]);
            holder.counter3 = 0;
        }
        if (holder.playButton4Pressed) {
            holder.playButton4.setBackground(holder.playButton4.getResources().getDrawable(R.drawable.button_red_circle));
            holder.playButton4.setText("\u25A0");
        } else {
            holder.playButton4.setBackground(holder.playButton4.getResources().getDrawable(R.drawable.button_blue_circle));
            holder.playButton4.setText("▶");
            holder.progressBar4.setProgress(0);
            holder.timeRemaining4.setText(holder.times[3]);
            holder.counter4 = 0;
        }
    }

    void resetAllButtons(FileManagerViewHolder holder) {
        holder.playButton1Pressed = false;
        holder.playButton2Pressed = false;
        holder.playButton3Pressed = false;
        holder.playButton4Pressed = false;
        holder.progressBar1.setProgress(0);
        holder.progressBar2.setProgress(0);
        holder.progressBar3.setProgress(0);
        holder.progressBar4.setProgress(0);
        holder.timeRemaining1.setText(holder.times[0]);
        holder.timeRemaining2.setText(holder.times[1]);
        holder.timeRemaining3.setText(holder.times[2]);
        holder.timeRemaining4.setText(holder.times[3]);
        holder.counter1 = 0;
        holder.counter2 = 0;
        holder.counter3 = 0;
        holder.counter4 = 0;
        changeButtons(holder);
    }

    private void startPlaying(FileManagerViewHolder holder) {
        player = new MediaPlayer();
        try {
            player.setDataSource(holder.fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("startPlaying()", "prepare() failed");
        }
    }

    void stopPlaying() {
        player.release();
        player = null;
    }

    private void changeAllTimeRemaining(FileManagerViewHolder holder, String[] files) {
        player = new MediaPlayer();
        for (int i = 1; i < 5; i++) {
            holder.fileName = files[i - 1];
            try {
                player.setDataSource(holder.fileName);
                player.prepare();
            } catch (IOException e) {
                Log.e("setDataSource()", "failed");
            }
            switch (i) {
                case 1:
                    holder.timeLeft1 = player.getDuration();
                    updateTimer(holder.timeLeft1, holder.timeRemaining1);
                    holder.times[0] = timeLeftCalculation(holder.timeLeft1);
                    break;
                case 2:
                    holder.timeLeft2 = player.getDuration();
                    updateTimer(holder.timeLeft2, holder.timeRemaining2);
                    holder.times[1] = timeLeftCalculation(holder.timeLeft2);
                    break;
                case 3:
                    holder.timeLeft3 = player.getDuration();
                    updateTimer(holder.timeLeft3, holder.timeRemaining3);
                    holder.times[2] = timeLeftCalculation(holder.timeLeft3);
                    break;
                case 4:
                    holder.timeLeft4 = player.getDuration();
                    updateTimer(holder.timeLeft4, holder.timeRemaining4);
                    holder.times[3] = timeLeftCalculation(holder.timeLeft4);
                    break;
            }
            player.reset();
        }
        player = null;
    }

    private void updateTimer(long timeLeft, TextView timeRemaining) {
        String timeLeftText = timeLeftCalculation(timeLeft);
        timeRemaining.setText(timeLeftText);
    }

    private String timeLeftCalculation(long timeLeft) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        return String.format(Locale.getDefault(), "-%02d:%02d", minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return database.size();
    }

    static class FileManagerViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        Button playButton1;
        Button playButton2;
        Button playButton3;
        Button playButton4;

        ProgressBar progressBar1;
        ProgressBar progressBar2;
        ProgressBar progressBar3;
        ProgressBar progressBar4;

        TextView timeRemaining1;
        TextView timeRemaining2;
        TextView timeRemaining3;
        TextView timeRemaining4;

        Button shareButton;
        Button deleteButton;

        boolean playButton1Pressed = false;
        boolean playButton2Pressed = false;
        boolean playButton3Pressed = false;
        boolean playButton4Pressed = false;

        private String fileName = null;

        long timeLeft1 = 0;
        long timeLeft2 = 0;
        long timeLeft3 = 0;
        long timeLeft4 = 0;

        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;
        int counter4 = 0;
        CountDownTimer countDownTimer;

        String[] times = new String[4];

        public FileManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.file_name);
            playButton1 = itemView.findViewById(R.id.answer_play1);
            playButton2 = itemView.findViewById(R.id.answer_play2);
            playButton3 = itemView.findViewById(R.id.answer_play3);
            playButton4 = itemView.findViewById(R.id.answer_play4);
            progressBar1 = itemView.findViewById(R.id.timeline1);
            progressBar2 = itemView.findViewById(R.id.timeline2);
            progressBar3 = itemView.findViewById(R.id.timeline3);
            progressBar4 = itemView.findViewById(R.id.timeline4);
            timeRemaining1 = itemView.findViewById(R.id.time_remaining1);
            timeRemaining2 = itemView.findViewById(R.id.time_remaining2);
            timeRemaining3 = itemView.findViewById(R.id.time_remaining3);
            timeRemaining4 = itemView.findViewById(R.id.time_remaining4);
            shareButton = itemView.findViewById(R.id.share_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}