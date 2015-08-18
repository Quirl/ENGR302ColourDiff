import java.awt.Rectangle;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Context {
	public List<Rectangle> sections;
	public Map<String, float[][]> diffs;
	public Context(List<Rectangle> _sections, Map<String, float[][]> _diffs){
		sections = _sections;
		diffs = _diffs;
	}

	public void write(File f) throws IOException{
		try(DataOutputStream out = new DataOutputStream(new FileOutputStream(f))){
			out.writeInt(sections.size());
			out.writeInt(diffs.size());

			for(int i = 0;i < sections.size();i ++){
				Rectangle r = sections.get(i);
				out.writeInt(r.x);
				out.writeInt(r.y);
				out.writeInt(r.width);
				out.writeInt(r.height);
			}

			for(Map.Entry<String, float[][]> diff : diffs.entrySet()){
				out.writeUTF(diff.getKey());
				for(int i = 0;i < diff.getValue().length;i ++){
					for(int j = 0;j < diff.getValue()[i].length;j ++){
						out.writeFloat(diff.getValue()[i][j]);
					}
				}
			}
		}

	}
}
