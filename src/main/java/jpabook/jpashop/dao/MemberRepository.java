package jpabook.jpashop.dao;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;



@Repository
public class MemberRepository extends EntityManagerRepository<Member> {

    public MemberRepository(EntityManager em){
        super(em, Member.class);
    }

}
