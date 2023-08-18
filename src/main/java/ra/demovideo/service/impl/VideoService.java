package ra.demovideo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.demovideo.model.Video;
import ra.demovideo.service.IGenericService;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService implements IGenericService<Video,Long> {
    @Autowired
    private DataSource dataSource;
    @Override
    public List<Video> findAll() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Video> videos = new ArrayList<>();
        try {
            CallableStatement callSt = conn.prepareCall("{call FindAll}");
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                Video v = new Video();
                v.setId(rs.getLong("id"));
                v.setVideoUrl(rs.getString("video_url"));
                v.setTitle(rs.getString("title"));
                v.setDescription(rs.getString("description"));
                videos.add(v);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return videos;
    }

    @Override
    public void save(Video video) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (video.getId() == null) {
                // thêm moi
                CallableStatement callSt = conn.prepareCall("{call InsertVideo(?,?,?)}");
                callSt.setString(1,video.getVideoUrl());
                callSt.setString(2,video.getTitle());
                callSt.setString(3,video.getDescription());
                callSt.executeUpdate();
            } else {
                // cap nhat
                CallableStatement callSt = conn.prepareCall("{call UpdateVideo(?,?,?,?)}");
                callSt.setString(2,video.getVideoUrl());
                callSt.setString(3,video.getTitle());
                callSt.setString(4,video.getDescription());
                callSt.setLong(1,video.getId());
                callSt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void delete(Long id) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            // xóa ảnh phụ
            CallableStatement callSt = conn.prepareCall("{call DeleteVideo(?)}");
            callSt.setLong(1,id);
            callSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Video findById(Long id) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Video v = null;
        try {
            CallableStatement callSt = conn.prepareCall("{call FindById(?)}");
            callSt.setLong(1,id);
            ResultSet rs = callSt.executeQuery();
            while (rs.next()) {
                v = new Video();
                v.setId(rs.getLong("id"));
                v.setVideoUrl(rs.getString("video_url"));
                v.setTitle(rs.getString("title"));
                v.setDescription(rs.getString("description"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return v;
    }
}