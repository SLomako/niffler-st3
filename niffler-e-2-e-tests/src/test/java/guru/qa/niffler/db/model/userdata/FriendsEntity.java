package guru.qa.niffler.db.model.userdata;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
@IdClass(FriendsId.class)
public class FriendsEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserdataUserEntity user;

    @Id
    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private UserdataUserEntity friend;

    @Column(name = "pending")
    private boolean pending;

    public UserdataUserEntity getUser() {
        return user;
    }

    public void setUser(UserdataUserEntity user) {
        this.user = user;
    }

    public UserdataUserEntity getFriend() {
        return friend;
    }

    public void setFriend(UserdataUserEntity friend) {
        this.friend = friend;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
