package com.xmeme.handler;

import com.xmeme.clientobjects.ClientMeme;
import com.xmeme.pojo.MemeCreator;
import com.xmeme.pojo.Memes;
import com.xmeme.utils.Constants;
import com.xmeme.utils.HibernateUtil;
import com.xmeme.utils.IOCommonUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class MemeManager {
    /**
     * <p> get memes based on page order</p>
     * @param pageOrder page order is 1 or 2 or 3.
     * @return list of Memes from db
     */
    public List<Memes> getAllMemes(final int pageOrder) {
        Transaction tx = null;
        List<Memes> memeObject = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();
            TypedQuery<Memes> querys = getSelectAllMemeQuery(session, pageOrder);
            List<Memes> allMemess = querys.getResultList();
            if (IOCommonUtil.isValidList(allMemess)) {
                memeObject = allMemess;
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return memeObject;
    }


    private TypedQuery<Memes> getSelectAllMemeQuery(final Session session, final int pageOrder) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Memes> query = builder.createQuery(Memes.class);
        Root<Memes> root = query.from(Memes.class);
        query.select(root).orderBy(builder.desc(root.get("created_time")));
        return session.createQuery(query).setFirstResult(pageOrder*Constants.PAGE_ORDER_LIMIT).setMaxResults(Constants.PAGE_ORDER_LIMIT);
    }


    /**
     *
     * @param memeObjectDetails is the client meme object
     * @param creator is the creator details
     * @param memeID is the specific meme id to fetch
     * @return will return Meme from     database
     */
    public Memes getExistingMeme(final ClientMeme memeObjectDetails, final MemeCreator creator, final long memeID) {
        Transaction tx = null;
        Memes memeObject = null;
        try (Session session = HibernateUtil.getSession()) {
            tx = session.beginTransaction();

            TypedQuery<Memes> querys = getSelectQuery(session, memeObjectDetails, creator, memeID);
            List<Memes> allMemess = querys.getResultList();
            if (IOCommonUtil.isValidList(allMemess)) {
                memeObject = allMemess.get(0);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return memeObject;
    }

    /**
     *
     * @param memeObjectDetails is the client meme object
     * @param creator is the creator details
     * @param memeID is the specific meme id to fetch
     * @return will return typed Query to fetch memes
     */
    private TypedQuery<Memes> getSelectQuery(final Session session, final ClientMeme memeObjectDetails,
                                             final MemeCreator creator, final long memeID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Memes> query = builder.createQuery(Memes.class);
        Root<Memes> root = query.from(Memes.class);
        //Join<Memes, MemeCreator> join = root.join("owner_id");

        Predicate[] predicates = getPredications(builder, root, memeObjectDetails, creator, memeID);

        query.select(root).where(predicates);

        return session.createQuery(query);
    }

    /**
     *
     * @param memeObjectDetails is the client meme object
     * @param creator is the creator details
     * @param memeID is the specific meme id to fetch
     * @return  condition to fetch
     */
    private Predicate[] getPredications(final CriteriaBuilder builder, final Root<Memes> root,
                                        final ClientMeme memeObjectDetails,
                                        final MemeCreator creator, final long memeID) {

        if (IOCommonUtil.isValidObject(memeObjectDetails) && IOCommonUtil.isValidObject(creator)) {
            Predicate[] predicates = new Predicate[3];

            predicates[0] = builder.equal(root.get("owner_id"), creator.getOwnerID()); // NO I18N
            predicates[1] = builder.equal(root.get("url"), memeObjectDetails.getUrl()); // NO I18N
            predicates[2] = builder.equal(root.get("caption"), memeObjectDetails.getCaption()); // NO I18N
            return predicates;
        } else if (IOCommonUtil.isValidLong(memeID)) {
            Predicate[] predicates = new Predicate[1];
            predicates[0] = builder.equal(root.get("meme_id"), memeID); // NO I18N
            return predicates;
        }
        return null;
    }

    /**
     *
     * @param memeObjectDetails is the client meme object
     * @param creator is the creator details
     * @return will add Meme into database
     */
    public Memes addMeme(final ClientMeme memeObjectDetails, final MemeCreator creator) {
        Session session = HibernateUtil.getSession();
        org.hibernate.Transaction tr = session.beginTransaction();
        Memes memeCreated = new Memes(memeObjectDetails, creator);
        session.save(memeCreated);
        tr.commit();
        if (session != null) {
            session.close();
        }
        return memeCreated;
    }

    public Memes updateMeme(Memes updatedMeme) {
        Session session = HibernateUtil.getSession();
        org.hibernate.Transaction tr = session.beginTransaction();
        session.saveOrUpdate(updatedMeme);
        tr.commit();
        if (session != null) {
            session.close();
        }
        return updatedMeme;
    }

}
