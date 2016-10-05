package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by artisja on 10/3/2016.
 */

public class ContactsList {
    private class Node{

        private  Node nextNode;
        private  Node prevNode;
        private  Contact contactPerson;

        public Node(Contact contact,Node prev,Node next){
           contactPerson = contact;
            nextNode = next;
            prevNode = prev;
        }

        public Contact getContactPerson() {
            return contactPerson;
        }

        public Node getNextNode() {
            return nextNode;
        }

        public Node getPrevNode() {
            return prevNode;
        }

        public void setContactPerson(Contact contactPerson) {
            this.contactPerson = contactPerson;
        }

        public void setNextNode(Node nextNode) {
            this.nextNode = nextNode;
        }



        public void setPrevNode(Node prevNode) {
            this.prevNode = prevNode;
        }
    }

    private Node head;
    private Contact contact;
    int size;

    public ContactsList(){
        head = new Node(null,null,null);
        size=0;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getSize() {
        return size;
    }

    public Node getHead() {
        return head;
    }

    public void addContact(Contact contact){
        Node newNode = new Node(contact,null,null);
        if (head.getNextNode()==null){
            head.setNextNode(newNode);
            newNode.setPrevNode(head);
        }else if (head.getNextNode()!=null){
            Node currentNode = head;
            while (currentNode.getNextNode()!=null){
                currentNode.setNextNode(currentNode.getNextNode());
            }
            currentNode.setNextNode(newNode);
            newNode.setPrevNode(currentNode);
        }
    }

    public void deleteContact(String name,String number){
        Node currentNode = head;
        while (currentNode.getNextNode()!=null){
            currentNode.setNextNode(currentNode.getNextNode());
            if (currentNode.getContactPerson().getName().equals(name) && currentNode.getContactPerson().getNumber().equals(number)){
                currentNode.getPrevNode().setNextNode(currentNode.getNextNode());
                currentNode.getNextNode().setPrevNode(currentNode.getPrevNode());
                currentNode.setNextNode(null);
                currentNode.setPrevNode(null);
            }
        }
    }
}
