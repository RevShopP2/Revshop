package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import entity.CategoryEntity;
import util.DbConnect;
public class CategoryDao {
	private static Connection con;
	public CategoryDao(){
		con=DbConnect.getInstance().getConnection();
	}
	public void addCategory(CategoryEntity categoryEntity_obj) {
		String sql="insert into category(s_id,cname) values(?,?)";
		try(PreparedStatement stmt = con.prepareStatement(sql)){
			stmt.setInt(1, categoryEntity_obj.getS_id());
			stmt.setString(2, categoryEntity_obj.getCname());
			int res_insert=stmt.executeUpdate();
			if(res_insert==1) {
				System.out.println("Continue");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

