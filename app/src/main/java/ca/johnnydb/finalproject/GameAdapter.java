package ca.johnnydb.finalproject;

  import androidx.annotation.NonNull;
  import androidx.annotation.Nullable;
  import androidx.preference.PreferenceManager;

  import android.content.Context;
  import android.content.SharedPreferences;
  import android.graphics.Color;
  import android.view.LayoutInflater;
  import android.view.View;
  import android.view.ViewGroup;
  import android.widget.ArrayAdapter;
  import android.widget.ImageView;
  import android.widget.TextView;
  import com.squareup.picasso.Picasso;
  import java.util.ArrayList;
  import java.util.EventListener;

///GameAdapter Allows a user to add a games to a listview
public class GameAdapter extends ArrayAdapter<Game> implements EventListener
{
  private Context context;
  private ArrayList<Game> gameList;

  public GameAdapter (Context context, ArrayList<Game> gameList)
  {
    super(context, R.layout.list_item, gameList);
    this.context = context;
    this.gameList = gameList;
  }

  @NonNull
  @Override
  public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
  {
    View view = convertView;

    if (view == null)
    {
      LayoutInflater vi = LayoutInflater.from(context);
      view = vi.inflate(R.layout.list_item, null);
    }

    final Game game = gameList.get(position);
    TextView tvTitle = view.findViewById(R.id.tvTitle);
    TextView tvPlatform = view.findViewById(R.id.tvPlatform);
    ImageView ivBoxart = view.findViewById(R.id.ivBoxart);

    tvTitle.setText(game.getTitle());
    tvPlatform.setText(game.getPlatform());
    Picasso.get()
      .load(game.getImage())
      .placeholder(R.drawable.ic_baseline_fireplace_24)
      .error(R.drawable.ic_baseline_fireplace_24).fit().centerCrop().into(ivBoxart);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    boolean dark = prefs.getBoolean("checkbox_dark", false);

    int tvTitle_color = dark ? Color.rgb(0, 188, 212) : Color.rgb(30, 30, 30);
    tvTitle.setTextColor(tvTitle_color);

    return view;
  }


}