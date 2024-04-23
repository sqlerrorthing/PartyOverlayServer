package me.oneqxz.partyoverlay.server.managers;

import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.sctructures.Party;
import me.oneqxz.partyoverlay.server.sctructures.PartyMember;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
public class PartyManager {

    private static final Color[] memberColors = new Color[]{
            new Color(127, 171, 246),
            new Color(96, 69, 228),
            new Color(2, 169, 68),
            new Color(66, 47, 163),
            new Color(26, 89, 183),
            new Color(128, 176, 52),
            new Color(159, 46, 2),
            new Color(188, 191, 16),
            new Color(252, 157, 5),
            new Color(184, 50, 14),
            new Color(27, 14, 232),
            new Color(205, 231, 115),
            new Color(213, 22, 61),
            new Color(109, 242, 133),
            new Color(126, 187, 141),
            new Color(9, 109, 15)
    };
    private static final int MAX_PARTY_MEMBERS = memberColors.length;

    private static PartyManager INSTANCE;
    private final List<Party> partyList = new LinkedList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);


    public Party createParty(ConnectedUser user, String partyName)
    {
        Party party = new Party(UUID.randomUUID(), partyName);
        party.getMembers().add(PartyMember.fromConnectedUser(user, true, getMemberColor(0)));

        this.addParty(party);
        return party;
    }

    private void addParty(Party party)
    {
        this.partyList.add(party);
        party.start(executor, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void removeParty(Party party) {
        this.partyList.remove(party);
        party.stop();
    }

    public void onUserDisconnect(ConnectedUser user)
    {
        Party userOnParty = this.partyList.stream().filter(party ->
                party.getMembers().stream().anyMatch(member -> member.getUser() == user)
        ).findFirst().orElse(null);

        if(userOnParty != null)
        {
            PartyMember member = userOnParty.connectedUserToPartyMember(user);
            userOnParty.removeConnectedMember(member);

            if(userOnParty.getMembers().isEmpty())
                 removeParty(userOnParty);
            else
                if(member.isOwner())
                    userOnParty.getFirstPartyMember().setOwner(true);

        }
    }

    public boolean isOnParty(ConnectedUser user)
    {
        return partyList.stream().anyMatch(party -> party.getMembers().stream().anyMatch(member -> member.getUser() == user));
    }

    public PartyMember getUserParty(ConnectedUser user)
    {
        for(Party party : partyList)
        {
            PartyMember member = party.getMembers().stream().filter(m -> m.getUser().equals(user)).findFirst().orElse(null);
            if(member != null)
                return member;
        }
        return null;
    }

    public static PartyManager getInstance() {
        return INSTANCE == null ? INSTANCE = new PartyManager() : INSTANCE;
    }

    public static Color getMemberColor(int index)
    {
        return memberColors[index];
    }
}
