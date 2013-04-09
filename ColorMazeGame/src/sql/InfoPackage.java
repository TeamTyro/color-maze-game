package sql;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import etc.Constants;

public class InfoPackage {
	private static String sTime, eTime, tTime;
	private static int [] _actions;
	
	public InfoPackage () {
		_actions = new int [500];
		for(int i=0; i<500; i++) {
			_actions[i] = 0;
		}
	}
	
	private static long parseInterval(final String s) {
        final Pattern p = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})$");
        final Matcher m = p.matcher(s);
        if (m.matches()) {
            final long hr = Long.parseLong(m.group(1)) * TimeUnit.HOURS.toMillis(1);
            final long min = Long.parseLong(m.group(2)) * TimeUnit.MINUTES.toMillis(1);
            final long sec = Long.parseLong(m.group(3)) * TimeUnit.SECONDS.toMillis(1);
            final long ms = Long.parseLong(m.group(4));
            return hr + min + sec + ms;
        } else {
            throw new IllegalArgumentException(s + " is not a supported interval format!");
        }
    }

    private static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }

    public static void getTotalTime(final String s1, final String s2) {

        final long i1 = parseInterval(s1);
        final long i2 = parseInterval(s2);

        tTime = formatInterval(i2 - i1);
        System.out.println(tTime);
    }
	
	public void setTimes(String s, String e) {
		sTime = s;
		eTime = e;
	}
	
	public void setActions(int [] a) {//sends in action array. a[] is the actions[]
		for(int i=0; i<500; i++) {
			if(a[i] != Constants.DIR_LEFT && a[i] != Constants.DIR_RIGHT && a[i] != Constants.DIR_UP && a[i] != Constants.DIR_DOWN && a[i] != 0) {
				System.out.printf("ERROR (IP): Unexpected action value!\n");
			} else {
				_actions[i] = a[i];
			}
		}
	}
	
	public int [] getActions() {
		return _actions;
	}
	
	public String getTime() {
		getTotalTime(sTime, eTime);
		return tTime;
	}
}
