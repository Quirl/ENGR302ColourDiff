import java.awt.Rectangle;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Context {
	public List<Rectangle> sections;
	public Map<String, HSBColor[]> diffs;
	public Context(List<Rectangle> _sections, Map<String, HSBColor[]> _diffs){
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

			for(Map.Entry<String, HSBColor[]> diff : diffs.entrySet()){
				out.writeUTF(diff.getKey());
				for(int i = 0;i < diff.getValue().length;i ++){
					out.writeFloat(diff.getValue()[i].h);
					out.writeFloat(diff.getValue()[i].s);
					out.writeFloat(diff.getValue()[i].b);
				}
			}
		}

	}
}
