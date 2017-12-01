package de.NeonSoft.neopowermenu.helpers;

public class GraphicItemHolder
{
		
		private String gName = null;
		private String gFile = null;
		private int gResource = -1;
		private String gFileName = null;
		
		public GraphicItemHolder() {
				
		}
		
		public GraphicItemHolder(String name, String file, int resource, String fileName) {
				gName = name;
				gFile = file;
				gResource = resource;
				gFileName = fileName;
		}
		
		public void setName(String name) {
				gName = name;
		}
		public String getName() {
				return gName;
		}

		public void setFile(String file) {
				gFile = file;
		}
		public String getFile() {
				return gFile;
		}

		public void setRessource(int ressource) {
				gResource = ressource;
		}
		public int getRessource() {
				return gResource;
		}

		public void setFileName(String fileName) {
				gFileName = fileName;
		}
		public String getFileName() {
				return gFileName;
		}
}
