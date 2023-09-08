package guru.qa.niffler.db.model.userdata;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
@IdClass(FriendsId.class)
public class FriendsEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserDataUserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private UserDataUserEntity friend;

    @Column(name = "pending")
    private boolean pending;

    public UserDataUserEntity getUser() {
        return user;
    }

    public void setUser(UserDataUserEntity user) {
        this.user = user;
    }

    public UserDataUserEntity getFriend() {
        return friend;
    }

    public void setFriend(UserDataUserEntity friend) {
        this.friend = friend;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
