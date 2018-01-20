package de.NeonSoft.neopowermenu.helpers;

import com.nostra13.universalimageloader.cache.disc.naming.*;

public class URLFileNameGenerator implements FileNameGenerator
{

		private final Md5FileNameGenerator mGenerator;

		public URLFileNameGenerator() {
				mGenerator = new Md5FileNameGenerator();
		}

		@Override
		public String generate(String p1)
		{

				if (p1==null )return null;
				return mGenerator.generate(p1.replaceFirst("file://",""));
		}

}
